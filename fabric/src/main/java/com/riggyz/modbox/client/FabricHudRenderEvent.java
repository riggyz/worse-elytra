package com.riggyz.modbox.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class FabricHudRenderEvent {

    public static void register() {
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            ElytraHudRenderer.render(graphics, tickDelta);
        });
    }
}