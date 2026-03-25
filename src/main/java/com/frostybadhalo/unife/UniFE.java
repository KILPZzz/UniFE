package com.frostybadhalo.unife;

import com.frostybadhalo.unife.registry.UnifeBlockEntities;
import com.frostybadhalo.unife.registry.UnifeBlocks;
import com.frostybadhalo.unife.registry.UnifeItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;


@Mod(UniFE.MODID)
public class UniFE {

    public static final String MODID = "unife";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UniFE(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        UnifeBlocks.BLOCKS.register(modEventBus);
        UnifeItems.ITEMS.register(modEventBus);
        UnifeBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);


    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
