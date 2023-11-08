package net.reimaden.voile.testmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer {

    public static final String MOD_ID = "voile-testmod";
    public static final String MOD_NAME = "Voile TestMod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("INITIALIZING TEST MOD");
    }
}
