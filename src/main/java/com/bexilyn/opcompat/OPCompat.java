package com.bexilyn.opcompat;

import com.bexilyn.opcompat.registry.ModBlockEntities;
import com.bexilyn.opcompat.registry.ModBlocks;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OPCompat.MOD_ID)
public class OPCompat {
    public static final String MOD_ID = "opcompat";

    public static final Logger LOGGER = LogUtils.getLogger();

    public OPCompat(FMLJavaModLoadingContext context) {

        IEventBus modEventBus =
                context.getModEventBus();

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        LOGGER.info("Overhauled Pets Transmog Compat initialized.");
    }
}