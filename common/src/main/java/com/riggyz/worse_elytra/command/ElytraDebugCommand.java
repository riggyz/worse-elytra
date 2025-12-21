package com.riggyz.worse_elytra.command;

import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.worse_elytra.elytra.FlightDistanceTracker;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ElytraDebugCommand {

    private static final int OP_LEVEL = 2;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        //  TODO: this should only register if we are in dev
        dispatcher.register(Commands.literal("elytra")
                // OP-only commands (modify elytra state/durability)
                .then(Commands.literal("break")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .executes(context -> setDamagePercent(context.getSource(), 0.99f)))
                .then(Commands.literal("repair")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .executes(context -> repairFull(context.getSource())))
                .then(Commands.literal("cooldown")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .executes(context -> triggerCooldown(context.getSource())))
                .then(Commands.literal("degrade")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .executes(context -> forceDegradation(context.getSource())))
                .then(Commands.literal("state")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .then(Commands.literal("normal")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.NORMAL)))
                        .then(Commands.literal("ruffled")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.RUFFLED)))
                        .then(Commands.literal("withered")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.WITHERED)))
                        .then(Commands.literal("broken")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.BROKEN))))
                // Available to all players
                .then(Commands.literal("info")
                        .executes(context -> showInfo(context.getSource())))
                .then(Commands.literal("hud")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof Player player) {
                                FlightDistanceTracker.toggleDetailedHUD(player);
                                return 1;
                            }
                            return 0;
                        })));
    }

    private static int setDamagePercent(CommandSourceStack source, float percent) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        int maxDamage = chestItem.getMaxDamage();
        int newDamage = Math.min((int) (maxDamage * percent), maxDamage - 1);
        chestItem.setDamageValue(newDamage);

        int remainingHealth = maxDamage - newDamage;
        source.sendSuccess(() -> Component.literal(
                String.format("Set durability to %d/%d (%.0f%% health)",
                        remainingHealth, maxDamage, (1.0f - percent) * 100)),
                false);

        return 1;
    }

    private static int repairFull(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        ElytraStateHandler.fullyRepair(chestItem);
        source.sendSuccess(() -> Component.literal("Elytra fully repaired to NORMAL state! "), false);

        return 1;
    }

    private static int triggerCooldown(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        ElytraStateHandler.setCooldown(player, chestItem);
        int ticks = ElytraStateHandler.getCooldownDuration(chestItem);
        source.sendSuccess(() -> Component.literal(
                String.format("Triggered %. 1f second cooldown", ticks / 20.0)), false);

        return 1;
    }

    private static int forceDegradation(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        ElytraState oldState = ElytraStateHandler.getStateFromStack(chestItem);
        boolean degraded = ElytraStateHandler.onDurabilityDepleted(player, chestItem);

        if (degraded) {
            ElytraState newState = ElytraStateHandler.getStateFromStack(chestItem);
            source.sendSuccess(() -> Component.literal(
                    "Degraded elytra:  " + oldState.name() + " → " + newState.name()), false);
        } else {
            source.sendFailure(Component.literal("Elytra is already BROKEN!"));
        }

        return 1;
    }

    private static int setElytraState(CommandSourceStack source, ElytraState state) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        ElytraStateHandler.setState(chestItem, state);
        source.sendSuccess(() -> Component.literal("Set elytra state to " + state.name()), false);

        return 1;
    }

    private static int showInfo(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!ElytraStateHandler.isElytra(chestItem)) {
            source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            return 0;
        }

        ElytraState state = ElytraStateHandler.getStateFromStack(chestItem);
        int damage = chestItem.getDamageValue();
        int maxDamage = chestItem.getMaxDamage();
        boolean onCooldown = ElytraStateHandler.isOnCooldown(player, chestItem);

        source.sendSuccess(() -> Component.literal("=== Custom Elytra Info ==="), false);
        source.sendSuccess(() -> Component.literal("State: " + state.name()), false);
        source.sendSuccess(() -> Component.literal("Can Fly: " + state.canFly()), false);
        source.sendSuccess(() -> Component.literal("Durability: " + (maxDamage - damage) + "/" + maxDamage), false);
        source.sendSuccess(() -> Component.literal("Max Distance: " + (int) state.maxDistance + " blocks"), false);
        source.sendSuccess(() -> Component.literal("Cooldown Duration: " + (state.baseCooldownTicks / 20.0) + "s"),
                false);
        source.sendSuccess(() -> Component.literal("On Cooldown: " + onCooldown), false);

        return 1;
    }
}