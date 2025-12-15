package com.riggyz.modbox.client;

import com.riggyz.modbox.Constants;
import com.riggyz.modbox.ModItems;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ElytraItemProperties {

    public static void register() {
        ItemProperties.register(
                ModItems.CUSTOM_ELYTRA,
                new ResourceLocation(Constants.MOD_ID, "state"),
                (stack, level, entity, seed) -> {
                    if (!(stack.getItem() instanceof CustomElytraItem)) {
                        return 0.0f;
                    }

                    ElytraState state = ElytraStateHandler.getStateFromStack(stack);
                    return switch (state) {
                        case NORMAL -> 0.0f;
                        case RUFFLED -> 0.25f;
                        case WITHERED -> 0.5f;
                        case BROKEN -> 1.0f;
                    };
                });
    }
}
