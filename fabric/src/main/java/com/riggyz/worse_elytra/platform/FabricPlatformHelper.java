package com.riggyz.worse_elytra.platform;

import com.riggyz.worse_elytra.compat.TrinketsCompat;
import com.riggyz.worse_elytra.platform.services.IPlatformHelper;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric specific implementation of the platform helper
 * 
 * @see IPlatformHelper
 */
public class FabricPlatformHelper implements IPlatformHelper {

    private static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public ItemStack checkModdedSlots(Player player) {
        if (TRINKETS_LOADED) {
            return TrinketsCompat.findElytraInTrinkets(player);
        }

        return ItemStack.EMPTY;
    }
}
