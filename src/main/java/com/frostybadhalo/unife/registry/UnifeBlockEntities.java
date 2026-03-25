package com.frostybadhalo.unife.registry;

import com.frostybadhalo.unife.blocks.feconverter.FEConverterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UnifeBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "unife");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FEConverterBlockEntity>> FE_CONVERTER =
            BLOCK_ENTITIES.register("fe_converter", () ->
                    BlockEntityType.Builder
                            .of(FEConverterBlockEntity::new, UnifeBlocks.FE_CONVERTER.get())
                            .build(null)
            );
}