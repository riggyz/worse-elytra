package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.advancement.AdvancementTriggers;
import com.riggyz.worse_elytra.command.ElytraDebugCommand;

import net.minecraft.advancements.CriteriaTriggers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * The high-level Fabric specific class for this mod. Takes care of things that
 * can only happen in Fabric.
 * 
 * @see CommonClass
 */
public class WorseElytra implements ModInitializer {

    /**
     * The plubic method that initializes everything needed for both client and
     * server.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ElytraDebugCommand.register(dispatcher);
        });

        CriteriaTriggers.register(AdvancementTriggers.ELYTRA_DEGRADED);

        CommonClass.init();
    }
}
