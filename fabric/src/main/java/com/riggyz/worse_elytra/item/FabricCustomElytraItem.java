package com.riggyz.worse_elytra.item;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FabricCustomElytraItem extends CustomElytraItem implements FabricElytraItem {

    public FabricCustomElytraItem() {
        super(CustomElytraItem.createProperties());
    }

    public FabricCustomElytraItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        if (!CustomElytraItem.isFlyEnabled(entity, chestStack)) {
            return false;
        }

        if (tickElytra) {
            doVanillaElytraTick(entity, chestStack);
        }

        return true;
    }
}