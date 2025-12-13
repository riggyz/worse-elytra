package com.riggyz.modbox.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class CustomElytraItem extends ElytraItem {

    public CustomElytraItem(Properties props) {
        super(props);

        props.defaultDurability(324);
    }

    public static boolean isFlyEnabled(ItemStack elytraStack) {
      return elytraStack.getDamageValue() < elytraStack.getMaxDamage() - 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        // Let players repair this elytra using leather instead of phantom membranes
        return repairCandidate.is(net.minecraft.world.item.Items.LEATHER) || super.isValidRepairItem(stack, repairCandidate);
    }

    // @Override
    // public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
    //     // You can add conditions here (durability, NBT, curses, etc.)
    //     return stack.getDamageValue() < stack.getMaxDamage() - 1;
    // }

    // @Override
    // public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
    //     // Basic vanilla behavior
    //     if (!entity.level().isClientSide && (flightTicks + 1) % 20 == 0) {
    //         stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(EquipmentSlot.CHEST));
    //     }
    //     return true;
    // }
}


