package com.riggyz.modbox.mixin;

import com.riggyz.modbox.item.CustomElytraItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ElytraItemDamageMixin {

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract void setDamageValue(int damage);

    @Inject(method = "hurtAndBreak", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void modbox$preventBreakAndDegrade(
            int amount,
            T entity,
            Consumer<T> onBreak,
            CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;

        if (!(self.getItem() instanceof CustomElytraItem)) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        ci.cancel();

        int currentDamage = this.getDamageValue();
        int effectiveMax = self.getMaxDamage();
        int newDamage = currentDamage + amount;

        if (newDamage >= effectiveMax) {
            CustomElytraItem.handleDegradation(player, self);
        } else {
            this.setDamageValue(newDamage);
        }
    }
}