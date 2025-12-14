package com.riggyz.modbox.client;

import com.riggyz.modbox.Constants;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the elytra state HUD element next to the hotbar (like the attack indicator).
 * Uses a two-layer approach: desaturated background + partial colored fill.
 * 
 * Texture layout (elytra_hud.png):
 * Row 0 (v=0):  Colored state icons  [NORMAL][RUFFLED][WITHERED][BROKEN]
 * Row 1 (v=16): Desaturated versions [NORMAL][RUFFLED][WITHERED][BROKEN]
 */
public class ElytraHudRenderer {

    // Texture location
    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Constants.MOD_ID,
            "textures/gui/elytra_hud.png");

    // Sprite dimensions
    private static final int SPRITE_SIZE = 18;  // Match vanilla attack indicator size

    // Texture dimensions (4 states × 18px wide, 2 rows × 18px tall)
    private static final int TEXTURE_WIDTH = 72;
    private static final int TEXTURE_HEIGHT = 36;

    // Texture row offsets
    private static final int ROW_COLORED = 0;
    private static final int ROW_DESATURATED = SPRITE_SIZE;

    // Hotbar dimensions (vanilla values)
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_OFFSET_Y = 22;  // Distance from bottom of screen to hotbar top

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
        if (chestStack.isEmpty() || !(chestStack.getItem() instanceof CustomElytraItem)) {
            return;
        }

        // Don't render if F1 is pressed (hide HUD)
        if (mc.options.hideGui) {
            return;
        }

        // Get elytra state
        ElytraState state = ElytraStateHandler.getStateFromStack(chestStack);

        if (!ElytraStateHandler.isOnCooldown(player, chestStack))
        {
            return;
        }

        // Calculate position - right side of hotbar (mirroring attack indicator on left)
        // Attack indicator: left of hotbar at (screenWidth/2 - 91 - 22, ...)
        // Our indicator: right of hotbar at (screenWidth/2 + 91 + 6, ...)
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Base position: right of hotbar
        int x = (screenWidth / 2) + (HOTBAR_WIDTH / 2) + 6;  // 6px gap from hotbar edge
        int y = screenHeight - HOTBAR_OFFSET_Y + 2;

        // If attack indicator is set to HOTBAR and currently showing (attack on cooldown),
        // offset further right to avoid overlap
        AttackIndicatorStatus attackIndicator = mc.options.attackIndicator().get();
        float attackStrength = player.getAttackStrengthScale(0.0f);
        boolean attackIndicatorVisible = attackIndicator == AttackIndicatorStatus.HOTBAR && attackStrength < 1.0f;
        
        if (attackIndicatorVisible) {
            x += SPRITE_SIZE + 6;  // Move right by icon size + small gap
        }

        // Get cooldown from vanilla system (smooth with partialTick)
        float cooldownPercent = player.getCooldowns().getCooldownPercent(chestStack.getItem(), partialTick);

        // Render using attack indicator style
        renderAttackIndicatorStyle(graphics, x, y, state, cooldownPercent);
    }

    /**
     * Renders the elytra icon in vanilla attack indicator style.
     * - Always draws the desaturated (gray) background
     * - Draws colored version filling from bottom based on readiness
     */
    private static void renderAttackIndicatorStyle(GuiGraphics graphics, int x, int y, 
            ElytraState state, float cooldownPercent) {
        
        int stateIndex = getStateIndex(state);
        int u = stateIndex * SPRITE_SIZE;

        // Calculate fill amount (1.0 = ready, 0.0 = just triggered)
        // We want to show fill amount, not cooldown amount
        float fillPercent = 1.0f - cooldownPercent;

        if (fillPercent >= 1.0f) {
            // Fully ready - just draw the colored icon
            graphics.blit(
                    HUD_TEXTURE,
                    x, y,
                    u, ROW_COLORED,
                    SPRITE_SIZE, SPRITE_SIZE,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            // On cooldown - draw desaturated background + partial colored fill

            // Layer 1: Full desaturated background
            graphics.blit(
                    HUD_TEXTURE,
                    x, y,
                    u, ROW_DESATURATED,
                    SPRITE_SIZE, SPRITE_SIZE,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);

            // Layer 2: Partial colored overlay (fills from bottom)
            if (fillPercent > 0) {
                int filledHeight = (int) (fillPercent * (SPRITE_SIZE + 1));  // +1 for visual smoothness
                int yOffset = SPRITE_SIZE - filledHeight;

                graphics.blit(
                        HUD_TEXTURE,
                        x, y + yOffset,                    // Screen position (shifted down)
                        u, ROW_COLORED + yOffset,          // Texture position (also shifted)
                        SPRITE_SIZE, filledHeight,         // Partial height
                        TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
    }

    /**
     * Get sprite column index for state.
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