package com.riggyz.modbox;

import com.riggyz.modbox.command.ElytraDebugCommand;
import com.riggyz.modbox.item.ForgeCustomElytraItem;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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

        ITEMS.register(Constants.CUSTOM_ELYTRA_ID, () -> {
            Item customElytra = new ForgeCustomElytraItem();
            ModItems.CUSTOM_ELYTRA = customElytra;

            return customElytra;
        });

        ITEMS.register(eventBus);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        CommonClass.init();
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ElytraDebugCommand.register(event.getDispatcher());
    }
}
