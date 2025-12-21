package com.riggyz.worse_elytra.elytra;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.advancement.AdvancementTriggers;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * The common implementation of elytra mechanics. This class acts as a container
 * for mechanics that are needed either in common or in a specific modloader.
 */
public class CustomMechanics {

    /**
     * Result of a repair calculation. USed so that all expected return values can
     * be wrapped and provided.
     */
    public static class RepairResult {
        /** Result of the repair calc */
        public final ItemStack output;
        /** Count of how many materials should be used */
        public final int materialsUsed;
        /** Calculated XP cost */
        public final int xpCost;

        /**
         * Public constructor for the RepairResult class.
         * 
         * @param output item to use as a result
         * @param materialsUsed how many materials were used in crafting
         * @param xpCost how much XP should it cost
         */
        public RepairResult(ItemStack output, int materialsUsed, int xpCost) {
            this.output = output;
            this.materialsUsed = materialsUsed;
            this.xpCost = xpCost;
        }
    }

    // NOTE: vanilla mechanic overrides

    /**
     * The common custom implementation of the isFlyEnabled check. It works by
     * effectively checking if the elytra is on cooldown and if there is enough
     * durability.
     * 
     * It should be noted that the vanilla elytra stops flying when at 1 durability
     * so that the wings do not break. Here instead we let the state manager handle
     * keeping the item around after hitting 0 durability.
     * 
     * @param entity the entity trying to fly with an elytra
     * @param stack  the elytra item to check state for
     * 
     * @return whether the entity can fly with the given elytra
     */
    public static boolean isFlyEnabled(LivingEntity entity, ItemStack stack) {
        int effectiveMax = stack.getMaxDamage();
        boolean hasEnoughDurability = effectiveMax > 0 && stack.getDamageValue() < effectiveMax;
        boolean isOnCooldown = false;
        ElytraState state = StateHandler.getStateFromStack(stack);

        if (entity instanceof Player player) {
            isOnCooldown = player.getCooldowns().isOnCooldown(stack.getItem());
        }

        return hasEnoughDurability && !isOnCooldown && state.allowsFlight();
    }

    /**
     * The common implmentation of the custom anvil repair logic. It takes into
     * account both durabiliy and state of the given elytra, and returns the
     * expected result.
     * 
     * This does not overwrite standard all elytra repair calcs, rather it just
     * checks for phantom membranes.
     * 
     * TODO: this needs to check for other elytras, and the logic needs to be cleaned up
     * 
     * @param stack    lefthand side of the anvil items
     * @param material righthand side of the anvil items
     * 
     * @return either null or the calculated RepairResult class
     */
    public static RepairResult calculateRepair(ItemStack stack, ItemStack material) {
        if (!(stack.getItem() instanceof ElytraItem
                && !material.isEmpty()
                && material.is(Items.PHANTOM_MEMBRANE))) {
            return null;
        }

        ElytraState currentState = StateHandler.getStateFromStack(stack);
        int currentDamage = stack.getDamageValue();
        int remainingMaterials = material.getCount();

        // Nothing to repair
        if (currentState == ElytraState.NORMAL && currentDamage == 0) {
            return null;
        }

        // Track totals
        int totalMembranesUsed = 0;
        int totalXpCost = 0;
        ItemStack result = stack.copy();
        ElytraState workingState = currentState;
        int workingDamage = currentDamage;

        // Keep repairing/upgrading while we have materials and something to do
        while (remainingMaterials > 0) {
            boolean didSomething = false;

            // Step 1: Repair durability if damaged (and not BROKEN)
            if (workingDamage > 0 && workingState != ElytraState.BROKEN) {
                int membranesForDurability = Math.min(Constants.DURABILITY_REPAIR_COST, remainingMaterials);

                if (membranesForDurability > 0) {
                    // Calculate how much durability to restore (proportional)
                    float repairPercent = (float) membranesForDurability / Constants.DURABILITY_REPAIR_COST;
                    int durabilityToRestore = (int) (workingDamage * repairPercent);

                    // If using full cost, restore all damage (avoid rounding issues)
                    if (membranesForDurability >= Constants.DURABILITY_REPAIR_COST) {
                        durabilityToRestore = workingDamage;
                    }

                    workingDamage -= durabilityToRestore;
                    remainingMaterials -= membranesForDurability;
                    totalMembranesUsed += membranesForDurability;
                    totalXpCost += membranesForDurability * Constants.XP_PER_DURABILITY_MEMBRANE;
                    didSomething = true;
                }
            }

            // Step 2: If durability is full (or BROKEN), try state upgrade
            if ((workingDamage == 0 || workingState == ElytraState.BROKEN)
                    && workingState != ElytraState.NORMAL) {

                int stateUpgradeCost = workingState.repairCost;

                if (remainingMaterials >= stateUpgradeCost) {
                    workingState = workingState.repair();
                    workingDamage = 0; // Fresh durability for new state

                    remainingMaterials -= stateUpgradeCost;
                    totalMembranesUsed += stateUpgradeCost;
                    totalXpCost += stateUpgradeCost * Constants.XP_PER_UPGRADE_MEMBRANE;
                    didSomething = true;
                }
            }

            // If we couldn't do anything this loop, we're done
            if (!didSomething) {
                break;
            }
        }

        // If nothing changed, return null
        if (totalMembranesUsed == 0) {
            return null;
        }

        // Apply final state to result
        StateHandler.setState(result, workingState);
        result.setDamageValue(workingDamage);

        return new RepairResult(result, totalMembranesUsed, Math.min(totalXpCost, 39));
    }

    // NOTE: new mechanic implementations

    /**
     * The custom side effect of an elytra state degrading. This will kick the
     * player out of flight, play a sound, and spawn some particles.
     * 
     * @param player the player to target
     * @param stack  the eltra item to degrade
     */
    public static void handleDegradation(Player player, ItemStack stack) {
        ElytraState currentState = StateHandler.getStateFromStack(stack);
        boolean canDegrade = currentState != ElytraState.BROKEN;

        // degrade to next state
        if (canDegrade) {
            ElytraState newState = currentState.degrade();
            int degradationCooldown = currentState.baseCooldownTicks * 2;

            StateHandler.setState(stack, newState);
            stack.setDamageValue(0);
            player.getCooldowns().addCooldown(stack.getItem(), degradationCooldown);
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0f, 0.5f);

            // Spawn big puff of smoke for degradation
            spawnDegradationPuff(player);

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementTriggers.ELYTRA_DEGRADED.trigger(serverPlayer, newState);
            }

            if (player.isFallFlying()) {
                player.stopFallFlying();
            }
        }
    }

    /**
     * The custom side effect of an elytra becoming exhausted. It kicks the player
     * out of flight and sets a cooldown.
     * 
     * @param player the player to target
     * @param stack  the eltra item to degrade
     */
    public static void kickOutOfFlight(Player player, ItemStack stack) {
        ElytraState state = StateHandler.getStateFromStack(stack);

        player.getCooldowns().addCooldown(stack.getItem(), state.baseCooldownTicks);
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHULKER_BOX_CLOSE,
                SoundSource.PLAYERS,
                1.0f, 0.5f);

        if (player.isFallFlying()) {
            player.stopFallFlying();
        }
    }

    // NOTE: internal helper functions

    /**
     * Helper function that creates a particle puff around the player. Designed to
     * be used for when the elytra breaks mid-flight
     * 
     * @param player the player to spawn the particles around
     */
    private static void spawnDegradationPuff(Player player) {
        Level level = player.level();
        RandomSource random = level.getRandom();

        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();

        if (level instanceof ServerLevel serverLevel) {
            // Server side - send particles to all nearby clients
            // Large smoke burst
            serverLevel.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    x, y, z,
                    15, // particle count
                    0.75, 0.5, 0.75, // spread
                    0.05 // speed
            );

            // Regular smoke
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    x, y, z,
                    10,
                    0.75, 0.5, 0.75,
                    0.08);
        } else {
            // Client side fallback
            int particleCount = 15 + random.nextInt(11);
            for (int i = 0; i < particleCount; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 1.5;
                double offsetY = (random.nextDouble() - 0.5) * 1.0;
                double offsetZ = (random.nextDouble() - 0.5) * 1.5;

                if (random.nextBoolean()) {
                    level.addParticle(
                            ParticleTypes.LARGE_SMOKE,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            (random.nextDouble() - 0.5) * 0.1,
                            0.05 + random.nextDouble() * 0.05,
                            (random.nextDouble() - 0.5) * 0.1);
                } else {
                    level.addParticle(
                            ParticleTypes.SMOKE,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            (random.nextDouble() - 0.5) * 0.15,
                            0.03 + random.nextDouble() * 0.05,
                            (random.nextDouble() - 0.5) * 0.15);
                }
            }
        }
    }
}
