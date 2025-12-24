package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.CustomMechanics;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

/**
 * The Fabric-specific mixin that takes care of the elytra flight check.
 *
 * @see CustomMechanics
 * @see FabricElytraItem
 */
@Mixin(ElytraItem.class)
public abstract class CanElytraFlyMixin implements FabricElytraItem {

    /**
     * Injected method that runs our custom check and the vanilla flight.
     * 
     * This overrides because we are implementing the function, we cannot hook into
     * this with inject.
     * 
     * @param entity     the entity trying to fly with an elytra
     * @param chestStack the elytra item to check state for
     * @param tickElytra whether to perform the check or also run flight logic
     */
    @Override
    public boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        if (!CustomMechanics.isFlyEnabled(entity, chestStack)) {
            return false;
        }

        if (tickElytra) {
            doVanillaElytraTick(entity, chestStack);
        }

        return true;
    }
}
