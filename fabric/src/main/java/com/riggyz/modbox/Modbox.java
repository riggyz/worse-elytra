package com.riggyz.modbox;

import com.riggyz.modbox.Item.FabricCustomElytraItem;
import com.riggyz.modbox.client.FabricHudRenderEvent;
import com.riggyz.modbox.command.ElytraDebugCommand;
import com.riggyz.modbox.item.ModItems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Modbox implements ModInitializer {

    @Override
    public void onInitialize() {

        // Create instance
        Item customElytra = new FabricCustomElytraItem();

        ModItems.CUSTOM_ELYTRA = customElytra;

        // Register it
        Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(Constants.MOD_ID, "custom_elytra"),
                customElytra);

        // Register debug commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ElytraDebugCommand.register(dispatcher);
        });

        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
