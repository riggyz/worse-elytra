package com.riggyz.worse_elytra.mechanics;

import com.riggyz.worse_elytra.client.ElytraParticleEffects;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.advancement.AdvancementTriggers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The common implementation of elytra mechanic overrides. This class only
 * exists so that the modloader specific implementations have something to refer
 * to.
 */
public class CustomElytra {

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

    /**
     * TODO: finish this javadoc
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
            ElytraParticleEffects.spawnDegradationPuff(player);

            if (player instanceof ServerPlayer serverPlayer) {
                ElytraState newState = ElytraStateHandler.getStateFromStack(stack);
                AdvancementTriggers.ELYTRA_DEGRADED.trigger(serverPlayer, newState);
            }

            if (player.isFallFlying()) {
                player.stopFallFlying();
            }
        }
    }
}
