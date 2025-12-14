package com.riggyz.modbox.elytra;

import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

/**
 * Centralized repair logic for CustomElytraItem.
 * 
 * Repair costs:
 * - Durability repair: DURABILITY_REPAIR_COST membranes to fully repair (any
 * state)
 * - State upgrade: state. repairCost membranes
 * 
 * If enough membranes are provided, multiple repairs and upgrades
 * happen in one operation (bulk repair).
 */
public class ElytraRepairHandler {

    // ==================== CONFIG ====================

    public static final int DURABILITY_REPAIR_COST = 4;
    public static final int XP_PER_DURABILITY_MEMBRANE = 1;
    public static final int XP_PER_UPGRADE_MEMBRANE = 1;

    // ==================== PUBLIC API ====================

    public static boolean canHandle(ItemStack left, ItemStack right) {
        return left.getItem() instanceof CustomElytraItem
                && !right.isEmpty()
                && right.is(Items.PHANTOM_MEMBRANE);
    }

    @Nullable
    public static RepairResult calculateRepair(ItemStack elytra, ItemStack material) {
        if (!canHandle(elytra, material)) {
            return null;
        }

        ElytraState currentState = ElytraStateHandler.getStateFromStack(elytra);
        int currentDamage = elytra.getDamageValue();
        int remainingMaterials = material.getCount();

        // Nothing to repair
        if (currentState == ElytraState.NORMAL && currentDamage == 0) {
            return null;
        }

        // Track totals
        int totalMembranesUsed = 0;
        int totalXpCost = 0;
        ItemStack result = elytra.copy();
        ElytraState workingState = currentState;
        int workingDamage = currentDamage;

        // Keep repairing/upgrading while we have materials and something to do
        while (remainingMaterials > 0) {
            boolean didSomething = false;

            // Step 1: Repair durability if damaged (and not BROKEN)
            if (workingDamage > 0 && workingState != ElytraState.BROKEN) {
                int membranesForDurability = Math.min(DURABILITY_REPAIR_COST, remainingMaterials);

                if (membranesForDurability > 0) {
                    // Calculate how much durability to restore (proportional)
                    float repairPercent = (float) membranesForDurability / DURABILITY_REPAIR_COST;
                    int durabilityToRestore = (int) (workingDamage * repairPercent);

                    // If using full cost, restore all damage (avoid rounding issues)
                    if (membranesForDurability >= DURABILITY_REPAIR_COST) {
                        durabilityToRestore = workingDamage;
                    }

                    workingDamage -= durabilityToRestore;
                    remainingMaterials -= membranesForDurability;
                    totalMembranesUsed += membranesForDurability;
                    totalXpCost += membranesForDurability * XP_PER_DURABILITY_MEMBRANE;
                    didSomething = true;
                }
            }

            // Step 2: If durability is full (or BROKEN), try state upgrade
            if ((workingDamage == 0 || workingState == ElytraState.BROKEN)
                    && workingState != ElytraState.NORMAL) {

                int stateUpgradeCost = workingState.repairCost;

                if (remainingMaterials >= stateUpgradeCost) {
                    workingState = workingState.repair();
                    workingDamage = 0; // Fresh durability for new state

                    remainingMaterials -= stateUpgradeCost;
                    totalMembranesUsed += stateUpgradeCost;
                    totalXpCost += stateUpgradeCost * XP_PER_UPGRADE_MEMBRANE;
                    didSomething = true;
                }
            }

            // If we couldn't do anything this loop, we're done
            if (!didSomething) {
                break;
            }
        }

        // If nothing changed, return null
        if (totalMembranesUsed == 0) {
            return null;
        }

        // Apply final state to result
        ElytraStateHandler.setState(result, workingState);
        result.setDamageValue(workingDamage);

        return new RepairResult(result, totalMembranesUsed, Math.max(totalXpCost, 39));
    }

    // ==================== RESULT CLASS ====================

    /**
     * Result of a repair calculation.
     */
    public static class RepairResult {
        public final ItemStack output;
        public final int materialsUsed;
        public final int xpCost;

        public RepairResult(ItemStack output, int materialsUsed, int xpCost) {
            this.output = output;
            this.materialsUsed = materialsUsed;
            this.xpCost = xpCost;
        }
    }
}