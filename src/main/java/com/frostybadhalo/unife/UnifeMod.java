package com.frostybadhalo.unife;

import com.frostybadhalo.unife.registry.UnifeBlockEntities;
import com.frostybadhalo.unife.registry.UnifeBlocks;
import com.frostybadhalo.unife.registry.UnifeItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("unife")
public class UnifeMod {
    public UnifeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        UnifeBlocks.BLOCKS.register(bus);
        UnifeItems.ITEMS.register(bus);
        UnifeBlockEntities.BLOCK_ENTITIES.register(bus);
        bus.addListener(UnifeMod::onBuildCreativeTabs);
    }

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {

        ResourceLocation tabId = ResourceLocation.fromNamespaceAndPath("powergrid", "main");
        if (event.getTabKey().location().equals(tabId)) {
            event.accept(UnifeItems.FE_CONVERTER.get());

        }
    }
}