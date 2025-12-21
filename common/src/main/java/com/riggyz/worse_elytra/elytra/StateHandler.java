package com.riggyz.worse_elytra.elytra;

import com.riggyz.worse_elytra.Constants;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The core of the special state logic for the elytra. This manages the state
 * interactions, side effects are taken care of in CustomMechanics, but this
 * wraps anything to do with getting/setting state.
 */
public class StateHandler {

    /**
     * The core Enum type that handles state, currently it wraps things like
     * durability modifier, drag coefficient, flight distance, and item repair cost.
     */
    public enum ElytraState {
        // NOTE: Enum states
        /** Normal state for the elytra */
        NORMAL(1.00, 650.0, 100, 1.00, 0),
        /** The meh state, not bad, not great */
        RUFFLED(0.99, 450.0, 140, 0.75, 4),
        /** Last state before broken */
        WITHERED(0.98, 300.0, 180, 0.50, 8),
        /** Broken state, really only used to make sure evey state has an enum */
        BROKEN(0, 0, 0, 0, 16);

        /** Additional multipler for drag, gets multipled by the vanilla drag */
        public final double dragMultiplier;
        /** Max distance that can be flown for the given state */
        public final double maxDistance;
        /** Cooldown time (in ticks) before the wings can fly again */
        public final int baseCooldownTicks;
        /**
         * By how much to modify multiply the durability defined in {@link Constants} by
         */
        public final double durabilityMultiplier;
        /**
         * How many repair items are needed to get to this state from the previous state
         */
        public final int repairCost;

        /**
         * Public constuctor for the enum, should not be used anywhere but in
         * StateHandler to define new states.
         * 
         * @param dragMultiplier
         * @param maxDistance
         * @param baseCooldownTicks
         * @param durabilityMultiplier
         * @param repairCost
         */
        ElytraState(double dragMultiplier, double maxDistance, int baseCooldownTicks,
                double durabilityMultiplier, int repairCost) {
            this.dragMultiplier = dragMultiplier;
            this.maxDistance = maxDistance;
            this.baseCooldownTicks = baseCooldownTicks;
            this.durabilityMultiplier = durabilityMultiplier;
            this.repairCost = repairCost;
        }

        /**
         * Whether this state is allowed to fly.
         * 
         * @return true if can fly, false if cannot
         */
        public boolean allowsFlight() {
            return switch (this) {
                case NORMAL -> true;
                case RUFFLED -> true;
                case WITHERED -> true;
                case BROKEN -> false;
            };
        }

        /**
         * Gets the next state when breaking.
         * 
         * @return next state in the state chain
         */
        public ElytraState degrade() {
            return switch (this) {
                case NORMAL -> RUFFLED;
                case RUFFLED -> WITHERED;
                case WITHERED -> BROKEN;
                case BROKEN -> BROKEN;
            };
        }

        /**
         * Gets the next state when repairing.
         * 
         * @return next state in the state chain
         */
        public ElytraState repair() {
            return switch (this) {
                case BROKEN -> WITHERED;
                case WITHERED -> RUFFLED;
                case RUFFLED -> NORMAL;
                case NORMAL -> NORMAL;
            };
        }

        /**
         * Returns the index of the state.
         * 
         * @return index of the given state
         */
        public int getIndex() {
            return switch (this) {
                case NORMAL -> 0;
                case RUFFLED -> 1;
                case WITHERED -> 2;
                case BROKEN -> 3;
            };
        }

        /**
         * Get the effective max durability for this state.
         * 
         * @return durability for the state
         */
        public int getMaxDurability() {
            return (int) (Constants.ELYTRA_BASE_DURABILITY * durabilityMultiplier);
        }
    }

    /**
     * TODO: this really should be moved somwehere else
     * 
     * @param player foo
     * @param elytra foo
     * 
     * @return foo
     */
    public static boolean isOnCooldown(Player player, ItemStack elytra) {
        return player.getCooldowns().isOnCooldown(elytra.getItem());
    }

    // NOTE: public state helpers

    /**
     * Get the state stored in the ItemStack NBT.
     * 
     * @param stack the ItemStack to check
     * 
     * @return state of the given item
     */
    public static ElytraState getStateFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return ElytraState.NORMAL;

        String stateStr = tag.getString(Constants.ELYTRA_NBT_KEY);
        if (stateStr.isEmpty())
            return ElytraState.NORMAL;

        try {
            return ElytraState.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            return ElytraState.NORMAL;
        }
    }

    /**
     * Set the state on the given ItemStack.
     * 
     * @param stack the ItemStack to modify
     * @param state new elytra state
     */
    public static void setState(ItemStack stack, ElytraState state) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(Constants.ELYTRA_NBT_KEY, state.name());
    }
}