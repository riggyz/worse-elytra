package com.riggyz.worse_elytra.elytra;

import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.advancement.AdvancementTriggers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The common implementation of elytra mechanics. This class acts as a container
 * for mechanics that are needed either in common or in a specific modloader.
 */
public class CustomMechanics {

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

        if (entity instanceof Player player) {
            isOnCooldown = ElytraStateHandler.isOnCooldown(player, stack);
        }

        return hasEnoughDurability && !isOnCooldown;
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
        boolean degraded = ElytraStateHandler.onDurabilityDepleted(player, stack);

        if (degraded) {
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0f, 0.5f);

            // Spawn big puff of smoke for degradation
            spawnDegradationPuff(player);

            if (player instanceof ServerPlayer serverPlayer) {
                ElytraState newState = ElytraStateHandler.getStateFromStack(stack);
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
        player.stopFallFlying();
        ElytraStateHandler.setCooldown(player, stack);

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHULKER_BOX_CLOSE,
                SoundSource.PLAYERS,
                1.0f, 0.5f);
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
