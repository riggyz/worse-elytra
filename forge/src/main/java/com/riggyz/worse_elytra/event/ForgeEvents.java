package com.riggyz.worse_elytra.event;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.ElytraRepairHandler;
import com.riggyz.worse_elytra.elytra.ElytraRepairHandler.RepairResult;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
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