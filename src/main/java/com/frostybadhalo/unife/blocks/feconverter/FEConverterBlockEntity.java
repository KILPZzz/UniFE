package com.frostybadhalo.unife.blocks.feconverter;

import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.frostybadhalo.unife.registry.UnifeBlockEntities;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.patryk3211.powergrid.electricity.base.ElectricBehaviour;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.febridge.IFEBridgeHandler;
import org.patryk3211.powergrid.electricity.sim.node.VoltageSourceCoupling;
import net.minecraftforge.fml.ModList;
import java.util.List;

public class FEConverterBlockEntity extends ElectricBlockEntity {

    private VoltageSourceCoupling voltageSource;
    private long feBuffer = 0;

    private static float INTERNAL_RESISTANCE = 5.0f;
    private static int FE_PER_TICK = 15;
    private static float MAX_VOLTAGE = 120.0f;
    private static long MAX_BUFFER = 10000L;

    public FEConverterBlockEntity(BlockPos pos, BlockState state) {
        super(UnifeBlockEntities.FE_CONVERTER.get(), pos, state);
    }

    private static String getIETier(Block block) {
        if (!ModList.get().isLoaded("immersiveengineering")) return null;

        for (String tier : new String[]{"LV", "MV", "HV"}) {
            var connector = IEBlocks.Connectors.getEnergyConnector(tier, false);
            var relay     = IEBlocks.Connectors.getEnergyConnector(tier, true);
            if (connector != null && connector.get() == block) return tier;
            if (relay     != null && relay.get()     == block) return tier;
        }
        return null;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        electricBehaviour = new ElectricBehaviour(this) {
            @Override
            public void tick() {
                super.tick();
                if (getWorld() == null || getWorld().isClientSide) return;
                pullAndConvert();
            }
        };
        behaviours.add(electricBehaviour);
    }

    private void pullAndConvert() {

        long[] totalExtracted = {0};
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) continue;

            var neighborBE = level.getBlockEntity(worldPosition.relative(dir));
            if (neighborBE == null) continue;
            if (neighborBE instanceof FEConverterBlockEntity) continue;

            var capOpt = neighborBE.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());

            capOpt.ifPresent(storage -> {
                if (storage.canExtract()) {
                    int extracted = storage.extractEnergy(FE_PER_TICK, false);

                    Block neighborBlock = neighborBE.getBlockState().getBlock();
                    String tier = getIETier(neighborBlock);

                    int fePerTick;
                    if ("HV".equals(tier)) {
                        MAX_VOLTAGE = 1000.0f;
                        fePerTick = 4096;
                    } else if ("MV".equals(tier)) {
                        MAX_VOLTAGE = 720.0f;
                        fePerTick = 1024;
                    } else if ("LV".equals(tier)) {
                        MAX_VOLTAGE = 360.0f;
                        fePerTick = 256;
                    } else {
                        MAX_VOLTAGE = 120.0f;
                        fePerTick = 256;
                    }

                    extracted = extracted * fePerTick;
                    feBuffer += extracted;
                    totalExtracted[0] += extracted;
                }
            });
        }

        if (totalExtracted[0] == 0) {
            feBuffer = 0;
            if (voltageSource != null) {
                voltageSource.setVoltage(0.0f);
            }
            setChanged();
            return;
        }


        if (voltageSource == null) return;

        feBuffer = Math.min(feBuffer, MAX_BUFFER);

        float voltToFE = IFEBridgeHandler.voltToFE();
        if (voltToFE <= 0) return;

        float fillRatio = (float) feBuffer / MAX_BUFFER;
        float targetVoltage = fillRatio * MAX_VOLTAGE;

        float current = (float) Math.abs(voltageSource.getCurrent());
        float wattToFE = IFEBridgeHandler.wattToFE();
        long consumed = (long)(current * current * INTERNAL_RESISTANCE * wattToFE);
        feBuffer = Math.max(0, feBuffer - consumed);

        voltageSource.setVoltage(targetVoltage);
        setChanged();
    }

    @Override
    public void buildCircuit(IElectricEntity.CircuitBuilder builder) {
        builder.setTerminalCount(4);

        voltageSource = new VoltageSourceCoupling(
                builder.terminalNode(0),
                builder.terminalNode(1),
                INTERNAL_RESISTANCE,
                0.0f
        );
        builder.add(voltageSource);

        builder.connect(0.1f, builder.terminalNode(0), builder.terminalNode(2));
        builder.connect(0.1f, builder.terminalNode(1), builder.terminalNode(3));
    }

    /*
    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putLong("FEBuffer", feBuffer);
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        feBuffer = nbt.getLong("FEBuffer");
    } */
}