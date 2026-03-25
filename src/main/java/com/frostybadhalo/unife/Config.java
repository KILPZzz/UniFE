package com.frostybadhalo.unife;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;



public class Config {
    //private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    // Deactivated for now..
    //static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
