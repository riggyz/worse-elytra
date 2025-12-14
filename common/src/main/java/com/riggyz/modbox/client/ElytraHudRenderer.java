package com.riggyz.modbox.client;

import com.riggyz.modbox.Constants;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the elytra state HUD element between health and hunger bars.
 * Shows cooldown as a transparent overlay on the sprite (like item cooldowns).
 */
public class ElytraHudRenderer {

    // Texture location
    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Constants.MOD_ID,
            "textures/gui/elytra_hud.png");

    // Sprite dimensions
    private static final int SPRITE_WIDTH = 16;
    private static final int SPRITE_HEIGHT = 16;

    // Texture dimensions (for UV calculations)
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 32;

    // Cooldown overlay color (semi-transparent white/gray)
    private static final int COOLDOWN_OVERLAY_COLOR = 0xAAFFFFFF;

    /**
     * Called during HUD render to draw our custom element.
     */
    public static void render(GuiGraphics graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            return;
        }

        // Check if player is wearing our custom elytra
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof CustomElytraItem)) {
            return;
        }

        // Don't render if F1 is pressed (hide HUD)
        if (mc.options.hideGui) {
            return;
        }

        // Get elytra state
        ElytraState state = ElytraStateHandler.getStateFromStack(chestStack);

        // Calculate position (center of screen, between health and hunger)
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = (screenWidth / 2) - (SPRITE_WIDTH / 2);
        int y = screenHeight - 39 - SPRITE_HEIGHT;

        // Render the elytra state icon
        renderStateIcon(graphics, x, y, state);

        // Render cooldown overlay
        float cooldownPercent = getCooldownPercent(player, chestStack);
        if (cooldownPercent > 0) {
            renderCooldownOverlay(graphics, x, y, cooldownPercent);
        }
    }

    /**
     * Get the cooldown percentage (1.0 = full cooldown, 0.0 = ready).
     */
    private static float getCooldownPercent(Player player, ItemStack elytra) {
        int remainingCooldown = ElytraStateHandler.getRemainingCooldown(player, elytra);

        if (remainingCooldown <= 0) {
            return 0f;
        }

        ElytraState state = ElytraStateHandler.getStateFromStack(elytra);
        int maxCooldown = state.baseCooldownTicks;

        if (maxCooldown <= 0) {
            return 0f;
        }

        return (float) remainingCooldown / maxCooldown;
    }

    /**
     * Render the elytra state icon.
     */
    private static void renderStateIcon(GuiGraphics graphics, int x, int y, ElytraState state) {
        int u = getStateIndex(state) * SPRITE_WIDTH;
        int v = 0;

        graphics.blit(
                HUD_TEXTURE,
                x, y,
                u, v,
                SPRITE_WIDTH, SPRITE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /**
     * Render the cooldown overlay (fills from bottom to top like item cooldowns).
     */
    private static void renderCooldownOverlay(GuiGraphics graphics, int x, int y, float percent) {
        // Calculate overlay height (fills from bottom)
        int overlayHeight = Math.round(SPRITE_HEIGHT * percent);

        if (overlayHeight <= 0) {
            return;
        }

        // Draw from bottom of sprite upward
        int overlayY = y + (SPRITE_HEIGHT - overlayHeight);

        graphics.fill(
                x,
                overlayY,
                x + SPRITE_WIDTH,
                overlayY + overlayHeight,
                COOLDOWN_OVERLAY_COLOR);
    }

    /**
     * Get sprite index for state.
     */
    private static int getStateIndex(ElytraState state) {
        return switch (state) {
            case NORMAL -> 0;
            case RUFFLED -> 1;
            case WITHERED -> 2;
            case BROKEN -> 3;
        };
    }
}