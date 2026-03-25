package com.frostybadhalo.unife.registry;

import com.frostybadhalo.unife.blocks.feconverter.FEConverterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UnifeBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("unife");

    public static final DeferredBlock<FEConverterBlock> FE_CONVERTER =
            BLOCKS.registerBlock("fe_converter", FEConverterBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                            .explosionResistance(500.0f)
                            .requiresCorrectToolForDrops()
            );
}