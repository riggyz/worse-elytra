package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.item.CustomElytraItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMaxDamageMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void worse_elytra$getStateBasedMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof CustomElytraItem) {
            ItemStack self = (ItemStack) (Object) this;
            ElytraState state = ElytraStateHandler.getStateFromStack(self);
            cir.setReturnValue(state.getMaxDurability(Constants.ELYTRA_BASE_DURABILITY));
        }
    }
}