package com.frostybadhalo.unife.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UnifeItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, "unife");

    public static final RegistryObject<BlockItem> FE_CONVERTER =
        ITEMS.register("fe_converter", () -> new BlockItem(
            UnifeBlocks.FE_CONVERTER.get(),
            new Item.Properties()
        ));
}