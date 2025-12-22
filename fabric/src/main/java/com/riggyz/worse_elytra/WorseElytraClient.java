package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.client.ElytraHudRenderer;
import com.riggyz.worse_elytra.elytra.Helpers;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

/**
 * The high-level Fabric specific class for this mod. Takes care of things that
 * can only happen in the Fabric client.
 */
public class WorseElytraClient implements ClientModInitializer {

    /**
     * The public method that initializes everything that is client sepcific.
     */
    @Override
    public void onInitializeClient() {
        registerItemProperties();
        registerHudRenderer();
    }

    /**
     * Helper function that registers to the item properties. Done so that the
     * item will update textures based on the nbt data it contains.
     * 
     * My greatest shame is that this logic needs to be duplicated.
     */
    private static void registerItemProperties() {
        ItemProperties.register(
                Items.ELYTRA,
                new ResourceLocation(Constants.MOD_ID, "state"),
                (stack, level, entity, seed) -> {
                    if (!Helpers.isElytra(stack)) {
                        return 0.0f;
                    }

                    ElytraState state = StateHandler.getState(stack);
                    return switch (state) {
                        case NORMAL -> 0.0f;
                        case RUFFLED -> 0.25f;
                        case WITHERED -> 0.5f;
                        case BROKEN -> 1.0f;
                    };
                });
    }

    /**
     * Helper function that registers to the hud rendering event. Adds the elytra
     * cooldown animation next to the hotbar.
     * 
     * @see ElytraHudRenderer
     */
    private static void registerHudRenderer() {
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            ElytraHudRenderer.render(graphics, tickDelta);
        });
    }
}