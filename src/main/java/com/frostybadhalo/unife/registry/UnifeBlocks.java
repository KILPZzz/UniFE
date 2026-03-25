package com.frostybadhalo.unife.registry;

import com.frostybadhalo.unife.blocks.feconverter.FEConverterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UnifeBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, "unife");

    public static final RegistryObject<FEConverterBlock> FE_CONVERTER =
        BLOCKS.register("fe_converter", () -> new FEConverterBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f, 6.0f)
                .sound(SoundType.WOOD)
                .noOcclusion()
                    .explosionResistance(500.0f).requiresCorrectToolForDrops()

        ));
}