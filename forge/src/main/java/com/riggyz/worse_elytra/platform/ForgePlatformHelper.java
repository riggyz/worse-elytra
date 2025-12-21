package com.riggyz.worse_elytra.platform;

import com.riggyz.worse_elytra.platform.services.IPlatformHelper;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Forge specific implementation of the platform helper
 * 
 * @see IPlatformHelper
 */
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}