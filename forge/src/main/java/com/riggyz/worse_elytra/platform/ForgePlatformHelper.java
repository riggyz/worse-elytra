package com.riggyz.worse_elytra.platform;

import com.riggyz.worse_elytra.platform.services.IPlatformHelper;
import com.riggyz.worse_elytra.compat.CuriosCompat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Forge specific implementation of the platform helper
 * 
 * @see IPlatformHelper
 */
public class ForgePlatformHelper implements IPlatformHelper {

    private static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

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

    @Override
    public ItemStack checkModdedSlots(Player player) {
        // Then check Curios if available
        if (CURIOS_LOADED) {
            return CuriosCompat.findElytraInCurios(player);
        }

        return ItemStack.EMPTY;
    }
}