package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.advancement.AdvancementTriggers;
import com.riggyz.worse_elytra.command.ElytraDebugCommand;
import com.riggyz.worse_elytra.item.ForgeCustomElytraItem;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Constants.MOD_ID)
public class WorseElytra {

    public static IEventBus eventBus;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public WorseElytra() {
        eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(Constants.CUSTOM_ELYTRA_ID, () -> {
            Item customElytra = new ForgeCustomElytraItem();
            ModItems.CUSTOM_ELYTRA = customElytra;

            return customElytra;
        });

        ITEMS.register(eventBus);
        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        CommonClass.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CriteriaTriggers.register(AdvancementTriggers.ELYTRA_DEGRADED);
            Constants.LOG.info("Registered custom advancement triggers for Worse Elytra");
        });
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ElytraDebugCommand.register(event.getDispatcher());
    }
}
