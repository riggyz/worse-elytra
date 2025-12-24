package com.riggyz.worse_elytra.elytra;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;
import com.riggyz.worse_elytra.platform.Services;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                .then(repairWingsCommandBuilder())
                .then(degradeWingsCommandBuilder())
                .then(setStateCommandBuilder())
                // Available to all players
                .then(Commands.literal("hud")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof Player player) {
                                FlightDataHandler.toggleDebugHUD(player);
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
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> breakWingsCommandBuilder() {
        final float damagePercent = 0.99f;
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("break");
        command.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        command.executes(context -> {
            CommandSourceStack source = context.getSource();

            if (!(source.getEntity() instanceof Player player)) {
                source.sendFailure(Component.literal("Must be run by a player"));
                return 0;
            }

            if (!Helpers.isElytraEquipped(player)) {
                source.sendFailure(Component.literal("You must be wearing an Elytra"));
                return 0;
            }

            ItemStack elytraStack = Helpers.getEquippedElytra(player);
            int maxDamage = elytraStack.getMaxDamage();
            int newDamage = Math.min((int) (maxDamage * damagePercent), maxDamage - 1);
            elytraStack.setDamageValue(newDamage);

            int remainingHealth = maxDamage - newDamage;
            source.sendSuccess(() -> Component.literal(
                    String.format("Set durability to %d/%d (%.0f%% health)",
                            remainingHealth, maxDamage, (1.0f - damagePercent) * 100)),
                    false);

            return 1;
        });

        return command;
    }

    /**
     * Private builder that creates the repair subcommand. Takes care of
     * permissions and registering the name.
     * 
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> repairWingsCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("repair");
        command.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        command.executes(context -> {
            CommandSourceStack source = context.getSource();

            if (!(source.getEntity() instanceof Player player)) {
                source.sendFailure(Component.literal("Must be run by a player"));
                return 0;
            }

            if (!Helpers.isElytraEquipped(player)) {
                source.sendFailure(Component.literal("You must be wearing an Elytra"));
                return 0;
            }

            ItemStack elytraStack = Helpers.getEquippedElytra(player);
            StateHandler.setState(elytraStack, ElytraState.NORMAL);
            elytraStack.setDamageValue(0);
            source.sendSuccess(() -> Component.literal("Elytra fully repaired to NORMAL state! "), false);

            return 1;
        });

        return command;
    }

    /**
     * Private builder that creates the degrade subcommand. Takes care of
     * permissions and registering the name.
     * 
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> degradeWingsCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("degrade");
        command.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        command.executes(context -> {
            CommandSourceStack source = context.getSource();

            if (!(source.getEntity() instanceof Player player)) {
                source.sendFailure(Component.literal("Must be run by a player"));
                return 0;
            }

            if (!Helpers.isElytraEquipped(player)) {
                source.sendFailure(Component.literal("You must be wearing an Elytra"));
                return 0;
            }

            ItemStack elytraStack = Helpers.getEquippedElytra(player);
            ElytraState oldState = StateHandler.getState(elytraStack);
            CustomMechanics.handleDegradation(player, elytraStack);
            ElytraState newState = StateHandler.getState(elytraStack);
            if (oldState != newState) {

                source.sendSuccess(() -> Component.literal(
                        "Degraded elytra: " + oldState.name() + " → " + newState.name()), false);
            } else {
                source.sendFailure(Component.literal("Elytra is already BROKEN!"));
            }

            return 1;
        });

        return command;
    }

    /**
     * Private builder that creates the set state subcommands. Takes care of
     * permissions and registering the name.
     *
     * @return the command stack
     */
    private static LiteralArgumentBuilder<CommandSourceStack> setStateCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("state");
        commands.requires(source -> source.hasPermission(Constants.OP_LEVEL));

        // try to mitigate nesting
        for (ElytraState state : ElytraState.values()) {
            commands.then(Commands.literal(state.name()).executes(context -> {
                CommandSourceStack source = context.getSource();

                if (!(source.getEntity() instanceof Player player)) {
                    source.sendFailure(Component.literal("Must be run by a player"));
                    return 0;
                }

                if (!Helpers.isElytraEquipped(player)) {
                    source.sendFailure(Component.literal("You must be wearing an Elytra"));
                    return 0;
                }

                ItemStack elytraStack = Helpers.getEquippedElytra(player);
                StateHandler.setState(elytraStack, state);
                source.sendSuccess(() -> Component.literal("Set elytra state to " +
                        state.name()), false);

                return 0;
            }));
        }

        return commands;
    }
}