package com.frostybadhalo.unife.events;

import me.duquee.createutilities.blocks.voidtypes.battery.VoidBattery;
import me.duquee.createutilities.blocks.voidtypes.battery.VoidBatteryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber(modid = "unife", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidBatteryTracker {

    private static final ResourceLocation MARKER_KEY = ResourceLocation.fromNamespaceAndPath("unife", "void_battery_marker");
    private static final Map<ResourceKey<Level>, Set<VoidBatteryTileEntity>> tracked = new HashMap<>();
    private static int tick = 0;

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();

        if (!(be.getClass().getName().equals(
                "me.duquee.createutilities.blocks.voidtypes.battery.VoidBatteryTileEntity"))) {
            return;
        }


        event.addCapability(MARKER_KEY, new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(
                    @NotNull Capability<T> cap, @Nullable Direction side) {
                return LazyOptional.empty();
            }
        });

        pendingRegistration.add(be);
    }

    private static final List<BlockEntity> pendingRegistration = new ArrayList<>();


    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;

        tick++;


        if (!pendingRegistration.isEmpty()) {
            Iterator<BlockEntity> it = pendingRegistration.iterator();
            while (it.hasNext()) {
                BlockEntity be = it.next();
                if (be.isRemoved()) {
                    it.remove();
                    continue;
                }
                Level beLevel = be.getLevel();
                if (beLevel == null) continue;

                if (!beLevel.isClientSide() && be instanceof VoidBatteryTileEntity vb) {
                    tracked.computeIfAbsent(beLevel.dimension(), k -> new HashSet<>()).add(vb);
                    it.remove();
                } else if (beLevel.isClientSide()) {
                    it.remove();
                }
            }
        }

        if (tick % 5 != 0) return;

        Set<VoidBatteryTileEntity> batteries = tracked.get(level.dimension());
        if (batteries == null || batteries.isEmpty()) return;

        List<VoidBatteryTileEntity> snapshot = new ArrayList<>(batteries);
        for (VoidBatteryTileEntity self : snapshot) {

            if (self.isRemoved() || self.getLevel() == null) {
                batteries.remove(self);
                continue;
            }


            if (!level.equals(self.getLevel())) continue;

            VoidBattery battery = self.getBattery();
            if (battery == null || battery.getEnergyStored() <= 0) continue;

            pushEnergy(level, self.getBlockPos(), battery);
        }
    }


    private static void pushEnergy(ServerLevel level, BlockPos pos, VoidBattery battery) {
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (neighbor == null) continue;


            if (neighbor.getClass().getName().equals("me.duquee.createutilities.blocks.voidtypes.battery.VoidBatteryTileEntity")) {continue;}

            LazyOptional<IEnergyStorage> capOpt =
                    neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());

            capOpt.ifPresent(target -> {
                if (!target.canReceive()) return;

                int canExtract = battery.extractEnergy(4096, true);
                if (canExtract <= 0) return;

                int canReceive = target.receiveEnergy(canExtract, true);
                if (canReceive <= 0) return;

                int extracted = battery.extractEnergy(canReceive, false);
                target.receiveEnergy(extracted, false);
            });
        }
    }
}