package com.riggyz.modbox.Item;

import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.item.CustomElytraItem;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric-specific extension of CustomElytraItem that implements
 * Fabric API's elytra flight interface.
 */
public class FabricCustomElytraItem extends CustomElytraItem implements FabricElytraItem {

    public FabricCustomElytraItem() {
        super(CustomElytraItem.createProperties());
    }

    public FabricCustomElytraItem(Properties properties) {
        super(properties);
    }

    /**
     * Fabric API: Called to check if entity can use this item for elytra flight.
     */
    @Override
    public boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        // Check durability
        if (!isFlyEnabled(chestStack)) {
            return false;
        }

        // Check cooldown if entity is a player
        if (entity instanceof Player player) {
            if (ElytraStateHandler.isOnCooldown(player, chestStack)) {
                return false;
            }
        }

        // Handle the flight tick if requested
        if (tickElytra) {
            doVanillaElytraTick(entity, chestStack);
        }

        return true;
    }
}