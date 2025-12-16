package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.item.CustomElytraItem;
import com.riggyz.worse_elytra.ModItems;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeElytraItemProperties {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
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
                    }
            );
        });
    }
}
