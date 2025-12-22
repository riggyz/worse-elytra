package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.Helpers;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class that targets the generic travel method. This needs to be targeted
 * because Player inherits.
 */
@Mixin(LivingEntity.class)
public abstract class EntityTravelMixin {

    /**
     * Mixin method that applied our custom drag on top of the vanilla drag.
     * 
     * I really wanted to avoid this but I don't know how robust it is to target
     * setDeltaMovement in travel, seems fragile.
     * 
     * @param movementInput
     * @param ci
     */
    @Inject(method = "travel", at = @At("TAIL"))
    private void worse_elytra$applyElytraDrag(Vec3 movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.isFallFlying() || !(self instanceof Player player)) {
            return;
        }

        if (!Helpers.isElytraEquipped(player)) {
            return;
        }

        ElytraState state = StateHandler.getState(Helpers.getEquippedElytra(player));
        if (state.dragMultiplier >= 1.0) {
            return;
        }

        Vec3 currentMotion = player.getDeltaMovement();
        player.setDeltaMovement(
                currentMotion.x * state.dragMultiplier,
                currentMotion.y,
                currentMotion.z * state.dragMultiplier);
    }
}