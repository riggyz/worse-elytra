package com.riggyz.modbox.client;

import com.riggyz.modbox.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeHudRenderEvent {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // Render after the player health so we appear on top
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            ElytraHudRenderer.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}