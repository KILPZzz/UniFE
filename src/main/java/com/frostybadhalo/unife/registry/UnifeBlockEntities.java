package com.frostybadhalo.unife.registry;

import com.frostybadhalo.unife.blocks.feconverter.FEConverterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UnifeBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "unife");

    public static final RegistryObject<BlockEntityType<FEConverterBlockEntity>> FE_CONVERTER =
            BLOCK_ENTITIES.register("fe_converter", () ->
                    BlockEntityType.Builder
                            .of(FEConverterBlockEntity::new, UnifeBlocks.FE_CONVERTER.get())
                            .build(null)

            );
}