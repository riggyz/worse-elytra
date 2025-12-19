package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.item.CustomElytraItem;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ElytraHudRenderer {
    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Constants.MOD_ID,
            "textures/gui/elytra_hud.png");

    private static final int SPRITE_SIZE = 18;
    private static final int TEXTURE_WIDTH = 72;
    private static final int TEXTURE_HEIGHT = 36;
    private static final int ROW_COLORED = 0;
    private static final int ROW_DESATURATED = SPRITE_SIZE;

    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_OFFSET_Y = 22;

    public static void render(GuiGraphics graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            return;
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.isEmpty() || !(chestStack.getItem() instanceof CustomElytraItem)) {
            return;
        }

        if (mc.options.hideGui) {
            return;
        }

        ElytraState state = ElytraStateHandler.getStateFromStack(chestStack);
        if (!ElytraStateHandler.isOnCooldown(player, chestStack)) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = (screenWidth / 2) + (HOTBAR_WIDTH / 2) + 6;
        int y = screenHeight - HOTBAR_OFFSET_Y + 2;

        AttackIndicatorStatus attackIndicator = mc.options.attackIndicator().get();
        float attackStrength = player.getAttackStrengthScale(0.0f);
        boolean attackIndicatorVisible = attackIndicator == AttackIndicatorStatus.HOTBAR && attackStrength < 1.0f;
        if (attackIndicatorVisible) {
            x += SPRITE_SIZE + 6;
        }

        float cooldownPercent = player.getCooldowns().getCooldownPercent(chestStack.getItem(), partialTick);
        renderAttackIndicatorStyle(graphics, x, y, state, cooldownPercent);
    }

    private static void renderAttackIndicatorStyle(GuiGraphics graphics, int x, int y,
            ElytraState state, float cooldownPercent) {
        int stateIndex = getStateIndex(state);
        int u = stateIndex * SPRITE_SIZE;
        float fillPercent = 1.0f - cooldownPercent;

        if (fillPercent >= 1.0f) {
            graphics.blit(
                    HUD_TEXTURE,
                    x, y,
                    u, ROW_COLORED,
                    SPRITE_SIZE, SPRITE_SIZE,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            graphics.blit(
                    HUD_TEXTURE,
                    x, y,
                    u, ROW_DESATURATED,
                    SPRITE_SIZE, SPRITE_SIZE,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);

            if (fillPercent > 0) {
                int filledHeight = (int) (fillPercent * (SPRITE_SIZE + 1));
                int yOffset = SPRITE_SIZE - filledHeight;

                graphics.blit(
                        HUD_TEXTURE,
                        x, y + yOffset,
                        u, ROW_COLORED + yOffset,
                        SPRITE_SIZE, filledHeight,
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
    }

    private static int getStateIndex(ElytraState state) {
        return switch (state) {
            case NORMAL -> 0;
            case RUFFLED -> 1;
            case WITHERED -> 2;
            case BROKEN -> 3;
        };
    }
}