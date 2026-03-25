package com.frostybadhalo.unife.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.capabilities.Capabilities;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlock;
import org.patryk3211.powergrid.electricity.deviceconnector.forge.BridgeElectricBehaviourImpl;
import org.patryk3211.powergrid.electricity.deviceconnector.forge.FEBridgeEnergyStorage;
import org.patryk3211.powergrid.electricity.febridge.IFEBridgeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BridgeElectricBehaviourImpl.class, remap = false)
public class BridgeElectricBehaviourImplMixin {

    @Inject(method = "makeFEHandler", at = @At("HEAD"), cancellable = true)
    private static void allowExtractableHandler(BlockEntity be,
                                                CallbackInfoReturnable<IFEBridgeHandler> cir) {
        Direction facing = be.getBlockState().getValue(DeviceConnectorBlock.FACING);
        BlockPos neighborPos = be.getBlockPos().relative(facing);

        var storage = be.getLevel().getCapability( Capabilities.EnergyStorage.BLOCK, neighborPos, facing.getOpposite());

        if (storage != null && storage.canExtract() && !storage.canReceive()) {
            cir.setReturnValue(new FEBridgeEnergyStorage(be));
        }
    }
}