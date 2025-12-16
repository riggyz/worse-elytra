package com.riggyz.modbox;

import com.riggyz.modbox.command.ElytraDebugCommand;
import com.riggyz.modbox.item.FabricCustomElytraItem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Modbox implements ModInitializer {

    @Override
    public void onInitialize() {
        Item customElytra = new FabricCustomElytraItem();
        ModItems.CUSTOM_ELYTRA = customElytra;

        Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(Constants.MOD_ID, Constants.CUSTOM_ELYTRA_ID),
                customElytra);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ElytraDebugCommand.register(dispatcher);
        });

        CommonClass.init();
    }
}
