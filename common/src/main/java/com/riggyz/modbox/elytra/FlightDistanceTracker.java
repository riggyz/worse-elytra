package com.riggyz.modbox.elytra;

import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class FlightDistanceTracker {

    private static final Map<UUID, FlightData> FLIGHT_DATA = new WeakHashMap<>();

    public static class FlightData {
        public Vec3 lastPosition;
        public double totalDistance;
        public boolean wasFlying;

        public FlightData(Vec3 startPos) {
            this.lastPosition = startPos;
            this.totalDistance = 0;
            this.wasFlying = true;
        }
    }

    private static final Map<UUID, Boolean> DETAILED_HUD = new WeakHashMap<>();

    public static void toggleDetailedHUD(Player player) {
        UUID id = player.getUUID();
        boolean current = DETAILED_HUD.getOrDefault(id, false);
        DETAILED_HUD.put(id, !current);
    }

    /**
     * Called every tick for each player - tracks flight distance
     */
    public static void tick(Player player) {
        UUID playerId = player.getUUID();
        boolean isFlying = player.isFallFlying();
        FlightData data = FLIGHT_DATA.get(playerId);

        ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!ElytraStateHandler.isCustomElytra(elytra)) {
            FLIGHT_DATA.remove(playerId);
            return;
        }

        ElytraState state = ElytraStateHandler.getStateFromStack(elytra);

        // Can't fly if broken
        if (!state.canFly() && isFlying) {
            player.stopFallFlying();
            return;
        }

        if (isFlying) {
            if (data == null || !data.wasFlying) {
                // Just started flying
                data = new FlightData(player.position());
                FLIGHT_DATA.put(playerId, data);
            } else {
                // Continue tracking distance
                Vec3 currentPos = player.position();
                double distanceThisTick = currentPos.distanceTo(data.lastPosition);
                data.totalDistance += distanceThisTick;
                data.lastPosition = currentPos;

                // Check if exceeded max distance
                double maxDistance = state.maxDistance;
                if (data.totalDistance >= maxDistance) {
                    kickOutOfFlight(player, elytra, data);
                    return;
                }
            }

            if (DETAILED_HUD.getOrDefault(playerId, false)) {
                displayFlightHUD(player, elytra, state, data);
            }

        } else {
            FLIGHT_DATA.remove(playerId);
        }
    }

    /**
     * Display flight speed and stats on the action bar
     */
    private static void displayFlightHUD(Player player, ItemStack elytra, ElytraState state, FlightData data) {
        Vec3 velocity = player.getDeltaMovement();

        // Calculate speeds
        double horizontalSpeed = velocity.horizontalDistance();
        double verticalSpeed = velocity.y;
        double totalSpeed = velocity.length();

        // Convert to blocks per second (multiply by 20 ticks)
        double hSpeedBps = horizontalSpeed * 20;
        double vSpeedBps = verticalSpeed * 20;
        double totalBps = totalSpeed * 20;

        // Distance remaining
        double remaining = state.maxDistance - data.totalDistance;

        // Build the HUD message
        Component hud = Component.literal("")
                .append(Component.literal("Speed: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.1f", totalBps)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" b/s").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("H: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(String.format("%.1f", hSpeedBps)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("V: ")
                        .withStyle(verticalSpeed >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED))
                .append(Component.literal(String.format("%+.1f", vSpeedBps)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("Dist: ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(String.format("%.0f", remaining)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("/" + (int) state.maxDistance).withStyle(ChatFormatting.GRAY));

        player.displayClientMessage(hud, true);
    }

    /**
     * Force the player out of flight mode
     */
    private static void kickOutOfFlight(Player player, ItemStack elytra, FlightData data) {
        player.stopFallFlying();

        ElytraStateHandler.setCooldown(player, elytra);

        // Vec3 currentMotion = player.getDeltaMovement();
        // player.setDeltaMovement(
        //         currentMotion.x * 0.5,
        //         -0.5,
        //         currentMotion.z * 0.5);

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHULKER_BOX_CLOSE,
                SoundSource.PLAYERS,
                1.0f, 0.5f);

        data.wasFlying = false;
        data.totalDistance = 0;
    }

    // ==================== PUBLIC GETTERS ====================

    public static double getRemainingDistance(Player player) {
        FlightData data = FLIGHT_DATA.get(player.getUUID());
        ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);
        ElytraState state = ElytraStateHandler.getStateFromStack(elytra);

        if (data == null || !ElytraStateHandler.isCustomElytra(elytra)) {
            return state.maxDistance;
        }

        return Math.max(0, state.maxDistance - data.totalDistance);
    }

    public static double getDistanceFlown(Player player) {
        FlightData data = FLIGHT_DATA.get(player.getUUID());
        return data != null ? data.totalDistance : 0;
    }
}