package com.riggyz.modbox.elytra;

import com.riggyz.modbox.item.CustomElytraItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ElytraStateHandler {

    private static final String STATE_KEY = "elytra_state";
    private static final String COOLDOWN_END_TAG = "modbox:cooldown_end";

    // ==================== ELYTRA STATE ENUM ====================
    public enum ElytraState {
        NORMAL(1.00, 500.0, 100, 1.00, 0),
        RUFFLED(0.98, 400.0, 140, 0.75, 4),
        WITHERED(0.95, 300.0, 180, 0.50, 8),
        BROKEN(1.00, 0.0, 0, 0.00, 16);

        public final double dragMultiplier;
        public final double maxDistance;
        public final int baseCooldownTicks;
        public final double durabilityMultiplier;
        public final int repairCost; // Cost in phantom membranes (or whatever repair item)

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

        /**
         * Get the total repair cost to fully restore to NORMAL.
         */
        public int getTotalRepairCost() {
            int total = 0;
            ElytraState current = this;
            while (current != NORMAL) {
                total += current.repairCost;
                current = current.repair();
            }
            return total;
        }
    }

    /**
     * Set the cooldown on the elytra.
     */
    public static void setCooldown(Player player, ItemStack elytra) {
        ElytraState state = getStateFromStack(elytra);
        long cooldownEnd = player.level().getGameTime() + state.baseCooldownTicks;
        elytra.getOrCreateTag().putLong(COOLDOWN_END_TAG, cooldownEnd);

        player.getCooldowns().addCooldown(elytra.getItem(), state.baseCooldownTicks);
    }

    /**
     * Get the remaining cooldown ticks.
     * Returns 0 if not on cooldown.
     */
    public static int getRemainingCooldown(Player player, ItemStack elytra) {
        if (!elytra.hasTag()) {
            return 0;
        }

        long cooldownEnd = elytra.getOrCreateTag().getLong(COOLDOWN_END_TAG);
        long currentTime = player.level().getGameTime();
        long remaining = cooldownEnd - currentTime;

        return remaining > 0 ? (int) remaining : 0;
    }

    /**
     * Check if the elytra is on cooldown.
     */
    public static boolean isOnCooldown(Player player, ItemStack elytra) {
        return getRemainingCooldown(player, elytra) > 0;
    }

    /**
     * Clear the cooldown.
     */
    public static void clearCooldown(ItemStack elytra) {
        if (elytra.hasTag()) {
            elytra.getOrCreateTag().remove(COOLDOWN_END_TAG);
        }
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
     * Repair the elytra by one state level.
     * Returns true if repair was successful.
     */
    public static boolean repairOneLevel(ItemStack elytra) {
        ElytraState currentState = getStateFromStack(elytra);

        if (currentState == ElytraState.NORMAL) {
            return false;
        }

        ElytraState repairedState = currentState.repair();
        setState(elytra, repairedState);

        return true;
    }

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

    /**
     * Get remaining cooldown as a percentage (0.0 to 1.0)
     */
    public static float getCooldownPercent(Player player, ItemStack elytra) {
        return player.getCooldowns().getCooldownPercent(elytra.getItem(), 0.0f);
    }

    // ==================== STAT GETTERS ====================

    // public static float getMultiplier(Player player, ItemStack elytra) {
    // return getStateFromStack(elytra).multiplier;
    // }

    // public static float getMaxSpeed(Player player, ItemStack elytra) {
    // return getStateFromStack(elytra).maxSpeed;
    // }

    public static double getMaxFlightDistance(Player player, ItemStack elytra) {
        return getStateFromStack(elytra).maxDistance;
    }

    public static int getCooldownDuration(ItemStack elytra) {
        return getStateFromStack(elytra).baseCooldownTicks;
    }

    // ==================== FLIGHT CHECKS ====================

    /**
     * Check if the elytra can be used for flight
     */
    public static boolean canFly(Player player, ItemStack elytra) {
        ElytraState state = getStateFromStack(elytra);

        if (!state.canFly()) {
            return false;
        }

        if (isOnCooldown(player, elytra)) {
            return false;
        }

        if (!CustomElytraItem.isFlyEnabled(elytra)) {
            return false;
        }

        return true;
    }

    // ==================== HELPER METHODS ====================

    public static boolean isCustomElytra(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof CustomElytraItem;
    }
}