package com.frostybadhalo.unife.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
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
        BlockEntity neighbor = be.getLevel().getBlockEntity(be.getBlockPos().relative(facing));
        if (neighbor == null) return;

        LazyOptional<IEnergyStorage> cap = neighbor.getCapability(
            ForgeCapabilities.ENERGY, facing.getOpposite()
        );

        cap.ifPresent(storage -> {
            if (storage.canExtract() && !storage.canReceive()) {
                cir.setReturnValue(new FEBridgeEnergyStorage(be));
            }
        });
    }
}