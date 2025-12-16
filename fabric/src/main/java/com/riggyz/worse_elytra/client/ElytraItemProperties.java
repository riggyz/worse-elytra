package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.ModItems;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.item.CustomElytraItem;

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
