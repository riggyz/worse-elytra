package com.riggyz.worse_elytra.elytra;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Wrapper class that holds any static methods that are need to help with logic.
 * Technically not needed but it reduces the amount of logic errors that could
 * happen.
 */
public class Helpers {

    // NOTE: Simple logic helpers

    public static boolean isElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem;
    }

    /**
     * Checks whether the player has the elytra equipped. Useful to gate rendering
     * or mixin logic, and removes the need to extract what the player is using in
     * code that should not care.
     * 
     * TODO: this should also check for trinkets and curios down the line
     * 
     * @param entity the player to check
     * 
     * @return if the player has an elytra equipped
     */
    public static boolean isElytraEquipped(Player entity) {
        boolean result = false;
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        // check chest by default
        result = !chestStack.isEmpty() && isElytra(chestStack);

        return result;
    }

    public static boolean isElytraEquipped(LivingEntity entity) {
        boolean result = false;
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        // check chest by default
        result = !chestStack.isEmpty() && isElytra(chestStack);

        return result;
    }

    /**
     * Gets the "priority" elytra to check for logic. Item is derived in a
     * deterministic order to prevent desync'd logic. This will only apply when
     * there are curios or
     * trinkets.
     * 
     * TODO: this should also check for trinkets and curios down the line
     * 
     * @param entity the player to check
     * 
     * @return the ItemStack of the equipped elytra
     */
    public static ItemStack getEquippedElytra(Player entity) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        return chestStack;
    }

    public static ItemStack getEquippedElytra(LivingEntity entity) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        return chestStack;
    }

    // NOTE: Cooldown helpers

    /**
     * Checks whether the elytra is on cooldown for the given player.
     * 
     * This function is preferred as it doesn't rely on an ItemStack and instead
     * checks the elytra item exclusively.
     * 
     * @param player the player to check for cooldown
     * 
     * @return true if the elytra is on cooldown, false otherwise
     */
    public static boolean isElytraOnCooldown(Player player) {
        return player.getCooldowns().isOnCooldown(Items.ELYTRA);
    }

    /**
     * Gets the remaining cooldown in the form of a percentage for the elytra.
     * 
     * This function is preferred as it doesn't rely on an ItemStack and instead
     * checks the elytra item exclusively.
     * 
     * @param player      the player to check
     * @param partialTick passthrough to the vanilla getCooldownPercent
     * 
     * @return the percent of cooldown that has passed on elytra
     */
    public static float getElytraCooldownPercent(Player player, float partialTick) {
        return player.getCooldowns().getCooldownPercent(Items.ELYTRA, partialTick);
    }

    /**
     * Sets the cooldown for the elytra. Used to ensure that cooldown is set for the
     * elytra specifically.
     * 
     * @param player player to set cooldown on
     * @param ticks  duration of the cooldown
     */
    public static void setElytraCooldown(Player player, int ticks) {
        player.getCooldowns().addCooldown(Items.ELYTRA, ticks);
    }
}
