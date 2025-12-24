package com.riggyz.worse_elytra.compat;

import com.riggyz.worse_elytra.elytra.Helpers;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class will ONLY be loaded if Trinkets is present
 */
public class TrinketsCompat {

    /**
     * Find elytra in any Trinkets slot
     */
    public static ItemStack findElytraInTrinkets(Player player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

        if (component.isPresent()) {
            var equipped = component.get().getEquipped(stack -> Helpers.isElytra(stack));

            if (!equipped.isEmpty()) {
                return equipped.get(0).getB();
            }
        }

        return ItemStack.EMPTY;
    }
}