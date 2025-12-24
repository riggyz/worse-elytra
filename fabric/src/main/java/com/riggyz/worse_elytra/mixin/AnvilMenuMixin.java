package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.CustomMechanics;
import com.riggyz.worse_elytra.elytra.CustomMechanics.RepairResult;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The Fabric-specific mixin that takes care of the anvil repair logic.
 *
 * @see CustomMechanics
 * @see AnvilMenu
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private int repairItemCountCost;

    /**
     * Needed to implement the baseclass it extends. Really is not needed but I get
     * errors without it.
     * 
     * @param menuType    needed for super constructor
     * @param containerId needed for super constructor
     * @param inventory   needed for super constructor
     * @param access      needed for super constructor
     */
    private AnvilMenuMixin(MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(menuType, containerId, inventory, access);
    }

    /**
     * Injected method that handles what the elytra is allowed to be combined with.
     * 
     * @see ElytraRepairHandler
     * 
     * @param ci the meta mixin callback information
     */
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void worse_elytra$handleCustomElytraRepair(CallbackInfo ci) {
        ItemStack left = this.inputSlots.getItem(0);
        ItemStack right = this.inputSlots.getItem(1);

        RepairResult result = CustomMechanics.calculateRepair(left, right);

        if (result != null) {
            this.resultSlots.setItem(0, result.output);
            this.cost.set(result.xpCost);
            this.repairItemCountCost = result.materialsUsed;
            ci.cancel();
        }
    }
}