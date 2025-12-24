package com.riggyz.worse_elytra.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

import com.riggyz.worse_elytra.elytra.Helpers;

/**
 * This class will ONLY be loaded if Curios is present
 * DO NOT reference this class directly - use CuriosHelper instead
 */
public class CuriosCompat {

    /**
     * Find elytra in any Curios slot
     */
    public static ItemStack findElytraInCurios(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    // Find all curio items that are elytras
                    List<SlotResult> curios = handler.findCurios(stack -> Helpers.isElytra(stack));

                    // Return the first elytra found
                    if (!curios.isEmpty()) {
                        return curios.get(0).stack();
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }
}