package com.riggyz.worse_elytra.elytra;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

// TODO: trim the fat out of this class
public class ElytraStateHandler {

    private static final String STATE_KEY = "elytra_state";

    // ==================== ELYTRA STATE ENUM ====================
    public enum ElytraState {
        NORMAL(1.00, 650.0, 100, 1.00, 0),
        RUFFLED(0.99, 450.0, 140, 0.75, 4),
        WITHERED(0.98, 300.0, 180, 0.50, 8),
        BROKEN(1.00, 0.0, 0, 0.00, 16);

        public final double dragMultiplier;
        public final double maxDistance;
        public final int baseCooldownTicks;
        public final double durabilityMultiplier;
        public final int repairCost;

        ElytraState(double dragMultiplier, double maxDistance, int baseCooldownTicks,
                double durabilityMultiplier, int repairCost) {
            this.dragMultiplier = dragMultiplier;
            this.maxDistance = maxDistance;
            this.baseCooldownTicks = baseCooldownTicks;
            this.durabilityMultiplier = durabilityMultiplier;
            this.repairCost = repairCost;
        }

        public boolean canFly() {
            return this != BROKEN;
        }

        public ElytraState degrade() {
            return switch (this) {
                case NORMAL -> RUFFLED;
                case RUFFLED -> WITHERED;
                case WITHERED -> BROKEN;
                case BROKEN -> BROKEN;
            };
        }

        public ElytraState repair() {
            return switch (this) {
                case BROKEN -> WITHERED;
                case WITHERED -> RUFFLED;
                case RUFFLED -> NORMAL;
                case NORMAL -> NORMAL;
            };
        }

        /**
         * Get the effective max durability for this state.
         */
        public int getMaxDurability(int baseDurability) {
            return (int) (baseDurability * durabilityMultiplier);
        }

        // /**
        //  * Get the total repair cost to fully restore to NORMAL.
        //  */
        // public int getTotalRepairCost() {
        //     int total = 0;
        //     ElytraState current = this;
        //     while (current != NORMAL) {
        //         total += current.repairCost;
        //         current = current.repair();
        //     }
        //     return total;
        // }
    }

    /**
     * Set the cooldown on the elytra.
     */
    public static void setCooldown(Player player, ItemStack elytra) {
        ElytraState state = getStateFromStack(elytra);
        player.getCooldowns().addCooldown(elytra.getItem(), state.baseCooldownTicks);
    }

    // public static float getRemainingCooldown(Player player, ItemStack elytra) {
    //     return player.getCooldowns().getCooldownPercent(elytra.getItem(), 0.0f);
    // }

    public static boolean isOnCooldown(Player player, ItemStack elytra) {
        return player.getCooldowns().isOnCooldown(elytra.getItem());
    }

    // ==================== STATE METHODS ====================

    /**
     * Get the state stored in the ItemStack NBT
     */
    public static ElytraState getStateFromStack(ItemStack elytra) {
        CompoundTag tag = elytra.getTag();
        if (tag == null)
            return ElytraState.NORMAL;

        String stateStr = tag.getString(STATE_KEY);
        if (stateStr.isEmpty())
            return ElytraState.NORMAL;

        try {
            return ElytraState.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            return ElytraState.NORMAL;
        }
    }

    /**
     * Set the state on the ItemStack
     */
    public static void setState(ItemStack elytra, ElytraState state) {
        CompoundTag tag = elytra.getOrCreateTag();
        tag.putString(STATE_KEY, state.name());
    }

    // ==================== DEGRADATION ====================

    /**
     * Called when the elytra's durability reaches 0.
     * Degrades the state and restores durability.
     * Returns true if degraded, false if already broken.
     */
    public static boolean onDurabilityDepleted(Player player, ItemStack elytra) {
        ElytraState currentState = getStateFromStack(elytra);

        // If already broken, can't degrade further
        if (currentState == ElytraState.BROKEN) {
            return false;
        }

        // Degrade to next state
        ElytraState newState = currentState.degrade();
        setState(elytra, newState);

        // Restore durability to full
        elytra.setDamageValue(0);

        // Apply 2x cooldown for the degradation event
        int degradationCooldown = currentState.baseCooldownTicks * 2;
        setCooldown(player, elytra, degradationCooldown);

        return true;
    }

    // ==================== REPAIR ====================

    /**
     * Fully repair the elytra to NORMAL state
     */
    public static void fullyRepair(ItemStack elytra) {
        setState(elytra, ElytraState.NORMAL);
        elytra.setDamageValue(0);
    }

    // ==================== COOLDOWN METHODS ====================

    /**
     * Sets cooldown with a custom duration
     */
    public static void setCooldown(Player player, ItemStack elytra, int ticks) {
        player.getCooldowns().addCooldown(elytra.getItem(), ticks);
    }

    // ==================== HELPER METHODS ====================

    public static boolean isElytra(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ElytraItem;
    }
}