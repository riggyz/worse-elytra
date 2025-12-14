package com.riggyz.modbox.mixin;

import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ElytraFlightMixin {

    /**
     * Apply additional drag based on elytra state.
     * Runs after vanilla elytra physics.
     */
    @Inject(method = "travel", at = @At("TAIL"))
    private void modbox$applyElytraDrag(Vec3 movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isFallFlying()) {
            return;
        }

        if (!(self instanceof Player player)) {
            return;
        }

        ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isCustomElytra(elytra)) {
            return;
        }

        ElytraState state = ElytraStateHandler.getStateFromStack(elytra);

        // Skip if no extra drag needed
        if (state.dragMultiplier >= 1.0) {
            return;
        }

        Vec3 currentMotion = player.getDeltaMovement();

        // Apply extra drag to horizontal movement
        player.setDeltaMovement(
                currentMotion.x * state.dragMultiplier,
                currentMotion.y,
                currentMotion.z * state.dragMultiplier);
    }
}