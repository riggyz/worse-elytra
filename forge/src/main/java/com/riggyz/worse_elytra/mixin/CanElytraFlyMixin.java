package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.CustomMechanics;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The Forge-specific mixin that takes care of the elytra flight check.
 *
 * @see CustomMechanics
 * @see ElytraItem
 */
@Mixin(ElytraItem.class)
public class CanElytraFlyMixin {

    /**
     * Function wrapper for the Forge flight check logic.
     * 
     * This needs remap because Forge uses a seperate function to check flight, not
     * the vanilla mapping.
     * 
     * @param stack  the elytra item to check state for
     * @param entity the entity trying to fly with an elytra
     * @param cir    the mixin injection handler, allows us to return a bool
     */
    @Inject(method = "canElytraFly", at = @At("HEAD"), cancellable = true, remap = false)
    private void worse_elytra$customFlightCheck(ItemStack stack, LivingEntity entity,
            CallbackInfoReturnable<Boolean> cir) {
        boolean canFly = CustomMechanics.isFlyEnabled(entity, stack);
        cir.setReturnValue(canFly);
    }
}
