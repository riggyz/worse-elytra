package com.riggyz.modbox.mixin;

import com.riggyz.modbox.Constants;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;

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
    private void modbox$getStateBasedMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.getItem() instanceof CustomElytraItem) {
            ItemStack self = (ItemStack) (Object) this;
            ElytraState state = ElytraStateHandler.getStateFromStack(self);
            cir.setReturnValue(state.getMaxDurability(Constants.ELYTRA_BASE_DURABILITY));
        }
    }
}