package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeHudRenderEvent {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            ElytraHudRenderer.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}