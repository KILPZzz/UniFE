package com.frostybadhalo.unife.blocks.feconverter;

import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.frostybadhalo.unife.registry.UnifeBlockEntities;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import org.patryk3211.powergrid.electricity.base.ElectricBehaviour;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.febridge.IFEBridgeHandler;
import org.patryk3211.powergrid.electricity.sim.node.VoltageSourceCoupling;
import net.neoforged.fml.ModList;

import java.util.List;

public class FEConverterBlockEntity extends ElectricBlockEntity {

    private VoltageSourceCoupling voltageSource;
    private long feBuffer = 0;

    private static float INTERNAL_RESISTANCE = 5.0f;
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

            BlockPos neighborPos = worldPosition.relative(dir);
            var neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;
            if (neighborBE instanceof FEConverterBlockEntity) continue;


            var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());

            if (storage == null || !storage.canExtract()) continue;

            Block neighborBlock = neighborBE.getBlockState().getBlock();
            String tier = getIETier(neighborBlock);



            // yes , i forgot to explain but .. simple ->  better connector = more energy (got it?)
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

            int extracted = storage.extractEnergy(fePerTick, false);
            feBuffer += extracted;
            totalExtracted[0] += extracted;
        }

        if (totalExtracted[0] == 0) {
            feBuffer = 0;
            if (voltageSource != null) {
                voltageSource.setVoltage(0.0f);
            }
            setChanged();
            return;
        }

        /*
        *
        * Sooo.. i know, it is kinda OP if you think, i tried to balance but
        * i never went good with that so.. i probably going to make for you guys custom adjustments
        *                           :|
        * */

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

}