package com.frostybadhalo.unife.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.capabilities.Capabilities;
import org.patryk3211.powergrid.electricity.deviceconnector.forge.DeviceConnectorBlockImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DeviceConnectorBlockImpl.class, remap = false)
public class DeviceConnectorBlockImplMixin {

    @Inject(method = "hasEnergyStorage", at = @At("HEAD"), cancellable = true)
    private static void allowExtractable(Level world, BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        var storage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);

        if (storage != null && (storage.canReceive() || storage.canExtract())) {
            cir.setReturnValue(true);
        }
    }
}