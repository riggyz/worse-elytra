package com.riggyz.modbox.item;

import com.riggyz.modbox.Constants;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

public class CustomElytraItem extends ElytraItem {
    public static final int BASE_DURABILITY = 324;

    public static Properties createProperties() {
        return new Properties()
                .durability(BASE_DURABILITY)
                .rarity(Rarity.UNCOMMON);
    }

    public CustomElytraItem(Properties props) {
        super(props);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        // Calculate state-based max directly instead of relying on mixin
        ElytraState state = ElytraStateHandler.getStateFromStack(stack);
        int maxDamage = state.getMaxDurability(BASE_DURABILITY);
        int damage = stack.getDamageValue();

        if (maxDamage == 0)
            return 0;

        // Clamp damage to max (in case damage exceeds current state's max)
        damage = Math.min(damage, maxDamage);

        return Math.round(13.0f - ((float) damage * 13.0f / (float) maxDamage));
    }

    /**
     * Check if the elytra has enough durability to fly.
     */
    public static boolean isFlyEnabled(ItemStack stack) {
        int effectiveMax = stack.getMaxDamage();
        return effectiveMax > 0 && stack.getDamageValue() < effectiveMax;
    }

    /**
     * Get durability as a fraction (0.0 to 1.0) based on current state's max.
     */
    public static float getDurabilityPercent(ItemStack stack) {
        int effectiveMax = stack.getMaxDamage();
        if (effectiveMax <= 0)
            return 0f;

        int remaining = effectiveMax - stack.getDamageValue();
        return Math.max(0f, Math.min(1f, (float) remaining / effectiveMax));
    }

    /**
     * Called when durability would deplete.
     * Degrades state instead of breaking the item.
     */
    public static void handleDegradation(Player player, ItemStack elytra) {
        ElytraState oldState = ElytraStateHandler.getStateFromStack(elytra);

        boolean degraded = ElytraStateHandler.onDurabilityDepleted(player, elytra);

        if (degraded) {
            ElytraState newState = ElytraStateHandler.getStateFromStack(elytra);

            // Notify the player
            player.displayClientMessage(
                    Component.literal("Your elytra has degraded!  ")
                            .withStyle(ChatFormatting.RED)
                            .append(Component.literal(oldState.name())
                                    .withStyle(getStateColor(oldState)))
                            .append(" → ")
                            .append(Component.literal(newState.name())
                                    .withStyle(getStateColor(newState))),
                    false);

            // Play degradation sound
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0f, 0.5f);

            // Stop flying if currently flying
            if (player.isFallFlying()) {
                player.stopFallFlying();
            }
        }
    }

    private static ChatFormatting getStateColor(ElytraState state) {
        return switch (state) {
            case NORMAL -> ChatFormatting.GREEN;
            case RUFFLED -> ChatFormatting.YELLOW;
            case WITHERED -> ChatFormatting.RED;
            case BROKEN -> ChatFormatting.DARK_GRAY;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        ElytraState state = ElytraStateHandler.getStateFromStack(stack);

        tooltip.add(Component.translatable("tooltip.modbox.elytra_state")
                .append(Component.literal(state.name()).withStyle(getStateColor(state))));

        if (state.canFly()) {
            // Show drag as a percentage (e.g., 0.98 = 2% extra drag)
            int extraDragPercent = (int) ((1.0 - state.dragMultiplier) * 100);
            if (extraDragPercent > 0) {
                tooltip.add(Component.translatable("tooltip.modbox.extra_drag")
                        .append(Component.literal(extraDragPercent + "%")
                                .withStyle(ChatFormatting.RED)));
            }

            tooltip.add(Component.translatable("tooltip.modbox.max_flight_distance")
                    .append(Component.literal(String.format("%.0f blocks", state.maxDistance))
                            .withStyle(ChatFormatting.YELLOW)));

            tooltip.add(Component.translatable("tooltip.modbox.cooldown_duration")
                    .append(Component.literal(String.format("%.1fs", state.baseCooldownTicks / 20.0))
                            .withStyle(ChatFormatting.GRAY)));
        } else {
            tooltip.add(Component.literal("Cannot fly - needs repair!")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }
}
