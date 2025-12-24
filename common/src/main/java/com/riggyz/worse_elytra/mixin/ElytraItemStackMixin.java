package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.CustomMechanics;
import com.riggyz.worse_elytra.elytra.Helpers;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;
import com.riggyz.worse_elytra.Constants;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This is the set of mixins that are needed to modify a specific elytra item.
 * It's general counterpart is ElytraItemMxin.
 * 
 * @see ElytraItemMixin
 */
@Mixin(ItemStack.class)
public abstract class ElytraItemStackMixin {

    // NOTE: The mixins that change the damage mechanics

    /**
     * Mixin that is required in order to make sure that the elytra has the correct
     * max damage. If this is not set then things get funky with halth bar
     * rendering.
     * 
     * @param cir some mixin magic
     */
    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void worse_elytra$getStateBasedMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;

        if (Helpers.isElytra(self)) {
            ElytraState state = StateHandler.getState(self);
            cir.setReturnValue(state.getMaxDurability());
        }
    }

    /**
     * Mixin that changes the elytra item breaking mechanic. It preserves the basic
     * enchant functionality and instead of breaking, decrements the state of the
     * elytra nbt.
     *
     * @param amount  the amount of damage to take
     * @param entity  the entity wearing the elytra
     * @param onBreak callback to trigger when the item breaks
     * @param ci      not sure but seems related to mixins
     */
    @Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), cancellable = true)
    private <T extends LivingEntity> void worse_elytra$interceptBreakEvent(
            int amount,
            T entity,
            Consumer<T> onBreak,
            CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (!Helpers.isElytra(self) || !(entity instanceof Player player)) {
            return;
        }

        // Cancel the break and handle degradation instead
        ci.cancel();
        CustomMechanics.handleDegradation(player, self);
    }

    // NOTE: The mixins that change the visual item mechanics

    /**
     * Mixin that fixes a visual bug that can occur. When the item state changes the
     * bar width is not accurately updated, so this mixin fixes that by check
     * itemstate then returning the appropriate value.
     *
     * @param cir some mixin magic
     */
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    private void worse_elytra$barWidthDerivedFromState(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;

        if (!Helpers.isElytra(self)) {
            return;
        }

        ElytraState state = StateHandler.getState(self);
        int maxDamage = state.getMaxDurability();
        int damage = self.getDamageValue();

        if (maxDamage == 0) {
            cir.setReturnValue(0);
            return;
        }

        damage = Math.min(damage, maxDamage);
        cir.setReturnValue(Math.round(13.0f - ((float) damage * 13.0f / (float) maxDamage)));
    }

    /**
     * Mixin that adds the damage level prefix to the item name.
     * 
     * By doing it this way the prefix will apply to even a renamed item, so it
     * should be viewed as live appending the state rather than muatating the item
     * name.
     *
     * @param cir some mixin magic
     */
    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void worse_elytra$addStatePrefix(CallbackInfoReturnable<Component> cir) {
        ItemStack self = (ItemStack) (Object) this;

        if (!Helpers.isElytra(self)) {
            return;
        }

        ElytraState state = StateHandler.getState(self);
        Component baseName = cir.getReturnValue();

        String prefixKey = switch (state) {
            case RUFFLED -> Constants.ELYTRA_RUFFLED_PREFIX_KEY;
            case WITHERED -> Constants.ELYTRA_WITHERED_PREFIX_KEY;
            case BROKEN -> Constants.ELYTRA_BROKEN_PREFIX_KEY;
            default -> null;
        };

        if (prefixKey != null) {
            cir.setReturnValue(Component.translatable(prefixKey)
                    .append(" ")
                    .append(baseName));
        }
    }
}