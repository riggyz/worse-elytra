package com.riggyz.modbox;

import com.riggyz.modbox.item.CustomElytraItem;
import com.riggyz.modbox.item.ModItems;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Constants.MOD_ID)
public class Modbox {

    public static IEventBus eventBus;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public Modbox() {
        eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register("custom_elytra", () -> {
            // Create instance
            Item customElytra = new CustomElytraItem(
                    new Item.Properties().durability(432));

            ModItems.CUSTOM_ELYTRA = customElytra;

            return customElytra;
        });

        ITEMS.register(eventBus);

        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();
    }
}
