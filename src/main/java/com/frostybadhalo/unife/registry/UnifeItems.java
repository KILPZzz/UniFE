package com.frostybadhalo.unife.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UnifeItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems("unife");

    public static final DeferredItem<BlockItem> FE_CONVERTER =
            ITEMS.registerSimpleBlockItem("fe_converter", UnifeBlocks.FE_CONVERTER);
}