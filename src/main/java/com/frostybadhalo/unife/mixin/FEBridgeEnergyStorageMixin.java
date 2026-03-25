package com.frostybadhalo.unife.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlock;
import org.patryk3211.powergrid.electricity.deviceconnector.forge.FEBridgeEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FEBridgeEnergyStorage.class, remap = false)
public class FEBridgeEnergyStorageMixin {

    @Shadow private BlockEntity be;
    @Shadow public long amount;
    @Shadow public long capacity;

    @Inject(method = "moveEnergy", at = @At("HEAD"), cancellable = true)
    private void pullFromExtractable(CallbackInfoReturnable<Long> cir) {
        Direction facing = be.getBlockState().getValue(DeviceConnectorBlock.FACING);
        BlockEntity neighbour = be.getLevel().getBlockEntity(be.getBlockPos().relative(facing));
        if (neighbour == null) return;

        neighbour.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite()).ifPresent(handler -> {
            if (handler.canExtract() && !handler.canReceive()) {
                int extracted = handler.extractEnergy(360, false);
                if (extracted > 0) {
                    amount += extracted;
                    be.setChanged();
                }
                cir.setReturnValue((long) extracted);
            }
        });
    }
}