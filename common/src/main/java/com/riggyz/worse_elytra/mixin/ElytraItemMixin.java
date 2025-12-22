package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.Helpers;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class that targets the Item class. This needs to be targeted because
 * the item defined the hovertext, while ItemStack defines the name text.
 */
@Mixin(Item.class)
public class ElytraItemMixin {

    /**
     * Injected method that adds a row of lore text to the elytra item.
     * 
     * @param stack   the item to check against
     * @param level
     * @param tooltip the tooltip to modify
     * @param flag
     * @param ci      the mxixin callback handler
     */
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void worse_elytra$addElytraStateLore(
            ItemStack stack,
            Level level,
            List<Component> tooltip,
            TooltipFlag flag,
            CallbackInfo ci) {

        // Only affect elytras
        if (!Helpers.isElytra(stack)) {
            return;
        }

        ElytraState state = StateHandler.getState(stack);

        String loreKey = switch (state) {
            case NORMAL -> Constants.ELYTRA_NORMAL_LORE_KEY;
            case RUFFLED -> Constants.ELYTRA_RUFFLED_LORE_KEY;
            case WITHERED -> Constants.ELYTRA_WITHERED_LORE_KEY;
            case BROKEN -> Constants.ELYTRA_BROKEN_LORE_KEY;
        };

        tooltip.add(Component.translatable(loreKey)
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}
