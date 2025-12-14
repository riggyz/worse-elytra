package com.riggyz.modbox.item;

import com.riggyz.modbox.elytra.ElytraStateHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


/**
 * Forge-specific extension of CustomElytraItem that implements
 * Forge's elytra flight hooks.
 */
public class ForgeCustomElytraItem extends CustomElytraItem {

    public ForgeCustomElytraItem() {
        super(CustomElytraItem.createProperties());
    }

    public ForgeCustomElytraItem(Properties properties) {
        super(properties);
    }

    /**
     * Forge-specific: Called to check if this item allows elytra flight.
     */
    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        // Check durability
        if (!isFlyEnabled(stack)) {
            return false;
        }

        // Check cooldown if entity is a player
        if (entity instanceof Player player) {
            if (ElytraStateHandler.isOnCooldown(player, stack)) {
                return false;
            }
        }

        return true;
    }
}
