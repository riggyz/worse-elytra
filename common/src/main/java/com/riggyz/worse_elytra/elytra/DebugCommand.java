package com.riggyz.worse_elytra.elytra;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;
import com.riggyz.worse_elytra.mixin.PlayerFlightMixin;
import com.riggyz.worse_elytra.platform.Services;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Container class that defines helpers to register and use commands. Ties into
 * much of the other custom logic.
 */
public class DebugCommand {

    /**
     * Registers the custom debug commands that are used for testing. Only registers
     * commands when in an development environment.
     * 
     * @param dispatcher the command dispater to register to
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!Services.PLATFORM.isDevelopmentEnvironment()) {
            return;
        }

        dispatcher.register(Commands.literal("elytra")
                // OP-only commands (modify elytra state/durability)
                .then(breakWingsCommandBuilder())
                .then(Commands.literal("repair")
                        .requires(source -> source.hasPermission(Constants.OP_LEVEL))
                        .executes(context -> repairFull(context.getSource())))
                .then(Commands.literal("degrade")
                        .requires(source -> source.hasPermission(Constants.OP_LEVEL))
                        .executes(context -> forceDegradation(context.getSource())))
                .then(setStateCommandBuilder())
                // Available to all players
                .then(Commands.literal("hud")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof Player player) {
                                PlayerFlightMixin.toggleDebugHUD(player);
                                return 1;
                            }
                            return 0;
                        })));
    }

    // NOTE: private command builders

    /**
     * Private builder that creates the break subcommand. Takes care of
     * permissions and registering the name.
     * 
     * TODO: functionality needs to be re-implemented
     * 
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> breakWingsCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("break");
        command.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        command.executes(context -> {
            // TODO: this needs to be reimplemented
            // if (!(source.getEntity() instanceof Player player)) {
            // source.sendFailure(Component.literal("Must be run by a player"));
            // return 0;
            // }

            // ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

            // if (!StateHandler.isElytra(chestItem)) {
            // source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
            // return 0;
            // }

            // int maxDamage = chestItem.getMaxDamage();
            // int newDamage = Math.min((int) (maxDamage * percent), maxDamage - 1);
            // chestItem.setDamageValue(newDamage);

            // int remainingHealth = maxDamage - newDamage;
            // source.sendSuccess(() -> Component.literal(
            // String.format("Set durability to %d/%d (%.0f%% health)",
            // remainingHealth, maxDamage, (1.0f - percent) * 100)),
            // false);

            return 1;
        });

        return command;
    }

    private static int repairFull(CommandSourceStack source) {
        // TODO: this needs to be reimplemented
        // if (!(source.getEntity() instanceof Player player)) {
        // source.sendFailure(Component.literal("Must be run by a player"));
        // return 0;
        // }

        // ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        // if (!StateHandler.isElytra(chestItem)) {
        // source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
        // return 0;
        // }

        // StateHandler.fullyRepair(chestItem);
        // source.sendSuccess(() -> Component.literal("Elytra fully repaired to NORMAL
        // state! "), false);

        return 1;
    }

    private static int forceDegradation(CommandSourceStack source) {
        // TODO: this needs to be reimplemented

        // if (!(source.getEntity() instanceof Player player)) {
        // source.sendFailure(Component.literal("Must be run by a player"));
        // return 0;
        // }

        // ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

        // if (!StateHandler.isElytra(chestItem)) {
        // source.sendFailure(Component.literal("You must be wearing a Custom Elytra"));
        // return 0;
        // }

        // ElytraState oldState = StateHandler.getStateFromStack(chestItem);
        // boolean degraded = StateHandler.onDurabilityDepleted(player, chestItem);

        // if (degraded) {
        // ElytraState newState = StateHandler.getStateFromStack(chestItem);
        // source.sendSuccess(() -> Component.literal(
        // "Degraded elytra: " + oldState.name() + " → " + newState.name()), false);
        // } else {
        // source.sendFailure(Component.literal("Elytra is already BROKEN!"));
        // }

        return 1;
    }

    /**
     * Private builder that creates the set state subcommands. Takes care of
     * permissions and registering the name.
     * 
     * TODO: functionality needs to be re-implemented
     * 
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> setStateCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("state");
        commands.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        // try to mitigate nesting
        for (int i = 0; i < 4; i++) {
            // commands.then(Commands.literal("normal").executes(context -> {
            // // if (!(source.getEntity() instanceof Player player)) {
            // // source.sendFailure(Component.literal("Must be run by a player"));
            // // return 0;
            // // }

            // // ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);

            // // if (!StateHandler.isElytra(chestItem)) {
            // // source.sendFailure(Component.literal("You must be wearing a Custom
            // Elytra"));
            // // return 0;
            // // }

            // // StateHandler.setState(chestItem, state);
            // // source.sendSuccess(() -> Component.literal("Set elytra state to " +
            // // state.name()), false);

            // return 0;
            // }));
        }

        return commands;
    }
}