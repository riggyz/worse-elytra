package com.riggyz.worse_elytra.item;

import com.riggyz.worse_elytra.client.ElytraParticleEffects;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.advancement.AdvancementTriggers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class CustomElytraItem extends ElytraItem {
    public static Properties createProperties() {
        return new Properties()
                .durability(Constants.ELYTRA_BASE_DURABILITY)
                .rarity(Rarity.UNCOMMON);
    }

    public CustomElytraItem(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        ElytraState state = ElytraStateHandler.getStateFromStack(stack);
        Component baseName = super.getName(stack);

        String prefixKey = switch (state) {
            case RUFFLED -> Constants.ELYTRA_RUFFLED_PREFIX_KEY;
            case WITHERED -> Constants.ELYTRA_WITHERED_PREFIX_KEY;
            case BROKEN -> Constants.ELYTRA_BROKEN_PREFIX_KEY;
            default -> null;
        };

        if (prefixKey == null) {
            return baseName;
        }

        return Component.translatable(prefixKey).withStyle(getStateColor(state)).append(" ").append(baseName);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ElytraState state = ElytraStateHandler.getStateFromStack(stack);
        int maxDamage = state.getMaxDurability(Constants.ELYTRA_BASE_DURABILITY);
        int damage = stack.getDamageValue();

        if (maxDamage == 0)
            return 0;

        damage = Math.min(damage, maxDamage);
        return Math.round(13.0f - ((float) damage * 13.0f / (float) maxDamage));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        ElytraState state = ElytraStateHandler.getStateFromStack(stack);
        String loreKey = switch (state) {
            case NORMAL -> Constants.ELYTRA_NORMAL_LORE_KEY;
            case RUFFLED -> Constants.ELYTRA_RUFFLED_LORE_KEY;
            case WITHERED -> Constants.ELYTRA_WITHERED_LORE_KEY;
            case BROKEN -> Constants.ELYTRA_BROKEN_LORE_KEY;
        };

        tooltip.add(Component.translatable(loreKey).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }

    public static boolean isFlyEnabled(LivingEntity entity, ItemStack stack) {
        int effectiveMax = stack.getMaxDamage();
        boolean hasEnoughDurability = effectiveMax > 0 && stack.getDamageValue() < effectiveMax;
        boolean isOnCooldown = false;

        if (entity instanceof Player player) {
            isOnCooldown = ElytraStateHandler.isOnCooldown(player, stack);
        }

        return hasEnoughDurability && !isOnCooldown;
    }

    public static void handleDegradation(Player player, ItemStack elytra) {
        boolean degraded = ElytraStateHandler.onDurabilityDepleted(player, elytra);

        if (degraded) {
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0f, 0.5f);

            // Spawn big puff of smoke for degradation
            ElytraParticleEffects.spawnDegradationPuff(player);

            if (player instanceof ServerPlayer serverPlayer) {
                ElytraState newState = ElytraStateHandler.getStateFromStack(elytra);
                AdvancementTriggers.ELYTRA_DEGRADED.trigger(serverPlayer, newState);
            }

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

}
