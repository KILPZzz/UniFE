package com.frostybadhalo.unife.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(value = DeviceConnectorBlock.class, remap = false)
public class DeviceConnectorSurviveMixin {

    @Inject(
            method = "canSurvive",
            at = @At("HEAD"),
            cancellable = true
    )
    private void allowIECapacitor(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!(world instanceof Level level)) return;

        Direction facing = state.getValue(DeviceConnectorBlock.FACING);
        BlockPos neighborPos = pos.relative(facing);
        Direction neighborSide = facing.getOpposite();

        BlockEntity be = level.getBlockEntity(neighborPos);
        if (be == null) return;

        boolean hasEnergy = be.getCapability(ForgeCapabilities.ENERGY, neighborSide).isPresent();
        if (!hasEnergy) return;

        cir.setReturnValue(true);
    }
}