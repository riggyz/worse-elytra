package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Wrapper class to handle event subscription. Done this way to denote that
 * these care about client events not client+server events.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeHudRenderEvent {

    /**
     * Helper function that registers to the hud rendering event. Adds the elytra
     * cooldown animation next to the hotbar.
     * 
     * @see ElytraHudRenderer
     * 
     * @param event the client event to register to
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            ElytraHudRenderer.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}