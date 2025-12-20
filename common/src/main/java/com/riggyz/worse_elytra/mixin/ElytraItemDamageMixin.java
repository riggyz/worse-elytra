package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.item.CustomElytraItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ElytraItemDamageMixin {
    // Inject right before the onBreak consumer is called (when item would break)
    @Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), cancellable = true)
    private <T extends LivingEntity> void worse_elytra$handleBreaking(
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

        // Cancel the break and handle degradation instead
        ci.cancel();
        CustomElytraItem.handleDegradation(player, self);
    }
}