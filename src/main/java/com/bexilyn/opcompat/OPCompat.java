package com.bexilyn.opcompat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(OPCompat.MOD_ID)
public class OPCompat {
    public static final String MOD_ID = "opcompat";

    public static final Logger LOGGER = LogUtils.getLogger();

    public OPCompat() {
        LOGGER.info("Overhauled Pets Transmog Compat initialized.");
    }
}