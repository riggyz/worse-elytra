package com.riggyz.worse_elytra.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.Helpers;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Wrapper class to handle event subscription. Done this way to denote that
 * these care about client events not client+server events.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ElytraItemProperties {

    /**
     * Helper function that registers to the item properties event. Done so that the
     * item will update textures based on the nbt data it contains.
     * 
     * My greatest shame is that this logic needs to be duplicated.
     * 
     * @param event the client event to register to
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
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
        });
    }
}
