package com.frostybadhalo.unife.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DeviceConnectorBlock.class, remap = false)
public class DeviceConnectorSurviveMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void allowIECapacitor(BlockState state, LevelReader world, BlockPos pos,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (!(world instanceof Level level)) return;

        Direction facing = state.getValue(DeviceConnectorBlock.FACING);
        BlockPos neighborPos = pos.relative(facing);
        Direction neighborSide = facing.getOpposite();

        var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, neighborSide);

        if (storage != null) {cir.setReturnValue(true);}
    }
}