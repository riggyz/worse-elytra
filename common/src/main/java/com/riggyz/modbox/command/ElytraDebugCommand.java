package com.riggyz.modbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.elytra.FlightDistanceTracker;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ElytraDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("elytradebug")
                .then(Commands.literal("break")
                        .executes(context -> setDamagePercent(context.getSource(), 0.99f)))
                .then(Commands.literal("half")
                        .executes(context -> setDamagePercent(context.getSource(), 0.50f)))
                .then(Commands.literal("repair")
                        .executes(context -> repairFull(context.getSource())))
                .then(Commands.literal("cooldown")
                        .executes(context -> triggerCooldown(context.getSource())))
                .then(Commands.literal("degrade")
                        .executes(context -> forceDegradation(context.getSource())))
                .then(Commands.literal("state")
                        .then(Commands.literal("normal")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.NORMAL)))
                        .then(Commands.literal("ruffled")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.RUFFLED)))
                        .then(Commands.literal("withered")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.WITHERED)))
                        .then(Commands.literal("broken")
                                .executes(context -> setElytraState(context.getSource(), ElytraState.BROKEN))))
                .then(Commands.literal("info")
                        .executes(context -> showInfo(context.getSource())))
                .then(Commands.literal("hud")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            if (context.getSource().getEntity() instanceof Player player) {
                                FlightDistanceTracker.toggleDetailedHUD(player);
                                source.sendSuccess(() -> Component.literal("Toggled detailed HUD"), false);
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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

        if (!ElytraStateHandler.isCustomElytra(chestItem)) {
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
        // source.sendSuccess(() -> Component.literal("Speed Multiplier: " + (int) (state.multiplier * 100) + "%"), false);
        // source.sendSuccess(() -> Component.literal("Max Speed: " + state.maxSpeed), false);
        source.sendSuccess(() -> Component.literal("Max Distance: " + (int) state.maxDistance + " blocks"), false);
        source.sendSuccess(() -> Component.literal("Cooldown Duration: " + (state.baseCooldownTicks / 20.0) + "s"),
                false);
        source.sendSuccess(() -> Component.literal("On Cooldown: " + onCooldown), false);

        return 1;
    }
}