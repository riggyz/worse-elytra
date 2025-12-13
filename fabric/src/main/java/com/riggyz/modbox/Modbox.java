package com.riggyz.modbox;

import com.riggyz.modbox.Item.CustomFabricElytraItem;
import com.riggyz.modbox.item.ModItems;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Modbox implements ModInitializer {

    @Override
    public void onInitialize() {

        // Create instance
        Item customElytra = new CustomFabricElytraItem(
                new Item.Properties().durability(432));

        ModItems.CUSTOM_ELYTRA = customElytra;

        // Register it
        Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(Constants.MOD_ID, "custom_elytra"),
                customElytra);

        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
