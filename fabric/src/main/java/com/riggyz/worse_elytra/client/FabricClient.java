package com.riggyz.worse_elytra.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElytraItemProperties.register();
        FabricHudRenderEvent.register();
    }
}