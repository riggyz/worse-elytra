package com.riggyz.modbox.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ForgeCustomElytraItem extends CustomElytraItem {

    public ForgeCustomElytraItem() {
        super(CustomElytraItem.createProperties());
    }

    public ForgeCustomElytraItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return isFlyEnabled(entity, stack);
    }
}
