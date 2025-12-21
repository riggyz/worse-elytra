package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.advancement.AdvancementTriggers;
import com.riggyz.worse_elytra.command.ElytraDebugCommand;
import com.riggyz.worse_elytra.elytra.ElytraRepairHandler;
import com.riggyz.worse_elytra.elytra.ElytraRepairHandler.RepairResult;

import net.minecraft.advancements.CriteriaTriggers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The high-level Forge specific class for this mod. Takes care of things that
 * can only happen in Forge.
 * 
 * @see CommonClass
 */
@Mod(Constants.MOD_ID)
public class WorseElytra {

    public static IEventBus setupEventBus;
    public static IEventBus runtimeEventBus;

    /**
     * Public constructor, in charge of registering certain events to the
     * ModEventBus.
     */
    public WorseElytra() {
        setupEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        runtimeEventBus = MinecraftForge.EVENT_BUS;

        runtimeEventBus.addListener(this::advancementSetup);

        runtimeEventBus.addListener(this::onRegisterCommands);
        runtimeEventBus.addListener(this::onAnvilUpdate);

        CommonClass.init();
    }

    /**
     * Helper function to register advancements to the ModEventBus. The logic itself
     * is in common.
     * 
     * @see AdvancementTriggers
     * 
     * @param event the setup event to add work to
     */
    private void advancementSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CriteriaTriggers.register(AdvancementTriggers.ELYTRA_DEGRADED);
        });
    }

    /**
     * Helper function to register the elytra debug commands. The commands
     * themselves are in common.
     * 
     * @see ElytraDebugCommand
     * 
     * @param event the game event to ingest
     */
    private void onRegisterCommands(RegisterCommandsEvent event) {
        ElytraDebugCommand.register(event.getDispatcher());
    }

    /**
     * Helper function to register what should happen on elytra in anvil.
     * 
     * @see ElytraRepairHandler
     * 
     * @param event the game event to ingest
     */
    private void onAnvilUpdate(AnvilUpdateEvent event) {
        RepairResult result = ElytraRepairHandler.calculateRepair(
                event.getLeft(),
                event.getRight());

        if (result != null) {
            event.setOutput(result.output);
            event.setCost(result.xpCost);
            event.setMaterialCost(result.materialsUsed);
        }
    }
}
