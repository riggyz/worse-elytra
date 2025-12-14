package com.riggyz.modbox.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElytraItemProperties.register();
        FabricHudRenderEvent.register();
        // ... other client init
    }
}