package com.riggyz.worse_elytra.client;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Handles visual effects for the custom elytra, like smoke puffs on damage and degradation.
 */
public class ElytraParticleEffects {

    /**
     * Spawns a small puff of smoke when the elytra takes damage.
     * Called periodically during flight when durability is consumed.
     * Works on both client and server side.
     */
    public static void spawnExhaustPuff(Player player) {
        Level level = player.level();
        RandomSource random = level.getRandom();
        
        // Spawn a few smoke particles behind the player (at wing positions)
        double x = player.getX();
        double y = player.getY() + 1.0; // Roughly at back/wing level
        double z = player.getZ();
        
        // Get player's look direction to offset particles behind
        double lookX = -player.getLookAngle().x * 0.5;
        double lookZ = -player.getLookAngle().z * 0.5;
        
        if (level instanceof ServerLevel serverLevel) {
            // Server side - send particles to all nearby clients
            serverLevel.sendParticles(
                ParticleTypes.SMOKE,
                x + lookX,
                y,
                z + lookZ,
                3,  // particle count
                0.25, 0.15, 0.25,  // spread
                0.01  // speed
            );
        } else {
            // Client side fallback
            int particleCount = 2 + random.nextInt(3);
            for (int i = 0; i < particleCount; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.5;
                double offsetY = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.5;
                
                level.addParticle(
                    ParticleTypes.SMOKE,
                    x + lookX + offsetX,
                    y + offsetY,
                    z + lookZ + offsetZ,
                    0, 0.02, 0
                );
            }
        }
    }

    /**
     * Spawns a large puff of smoke when the elytra degrades to a worse state.
     * This is a dramatic effect to indicate significant damage.
     * Works on both client and server side.
     */
    public static void spawnDegradationPuff(Player player) {
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
                15,  // particle count
                0.75, 0.5, 0.75,  // spread
                0.05  // speed
            );
            
            // Regular smoke
            serverLevel.sendParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                10,
                0.75, 0.5, 0.75,
                0.08
            );
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
                        (random.nextDouble() - 0.5) * 0.1
                    );
                } else {
                    level.addParticle(
                        ParticleTypes.SMOKE,
                        x + offsetX,
                        y + offsetY,
                        z + offsetZ,
                        (random.nextDouble() - 0.5) * 0.15,
                        0.03 + random.nextDouble() * 0.05,
                        (random.nextDouble() - 0.5) * 0.15
                    );
                }
            }
        }
    }
}
