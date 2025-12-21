package com.riggyz.worse_elytra.mixin;

import com.riggyz.worse_elytra.elytra.CustomMechanics;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Mixin class that targets the Players. We target players specifically because
 * we need to check them for flight distance. Other entities can wear the
 * elytra, but only the player should be nerfed.
 */
@Mixin(Player.class)
public abstract class PlayerFlightMixin {

    private static final Map<UUID, FlightData> FLIGHT_DATA = new WeakHashMap<>();
    private static final Map<UUID, Boolean> HUD_ENABLED = new WeakHashMap<>();

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

    public static void toggleDebugHUD(Player player) {
        UUID id = player.getUUID();
        boolean current = HUD_ENABLED.getOrDefault(id, false);
        HUD_ENABLED.put(id, !current);
    }

    /**
     * Injected mehtod, just serves as a wrapper to call flight tracker on the
     * server every tick
     * 
     * @see FlightDistanceTracker
     * 
     * @param ci mixin callback handler
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void worse_elytra$onPlayerTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide()) {
            return;
        }

        UUID playerId = player.getUUID();
        boolean isFlying = player.isFallFlying();
        FlightData data = FLIGHT_DATA.get(playerId);

        ItemStack elytra = player.getItemBySlot(EquipmentSlot.CHEST);
        // if (!ElytraStateHandler.isCustomElytra(elytra)) {
        // FLIGHT_DATA.remove(playerId);
        // return;
        // }

        ElytraState state = ElytraStateHandler.getStateFromStack(elytra);

        // Can't fly if broken
        // FIXME: wtf is this for????
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
                    data.wasFlying = false;
                    data.totalDistance = 0;
                    CustomMechanics.kickOutOfFlight(player, elytra);
                    return;
                }
            }

            if (HUD_ENABLED.getOrDefault(playerId, false)) {
                displayDebugHUD(player, elytra, state, data);
            }

        }
        // TODO: very hacky fix, need to check more states
        else if (data != null && data.wasFlying && !player.onGround()) {
            // do nothing
        } else {
            FLIGHT_DATA.remove(playerId);
        }
    }

    /**
     * Displays a server side calculated debug HUD so that movement vectors can be
     * seen. Is only for testing.
     * 
     * @param player the player to render to
     * @param elytra the item being used to fly
     * @param state  the in tick state of the item
     * @param data   the in-tick flight data to use to render
     */
    private static void displayDebugHUD(Player player, ItemStack elytra, ElytraState state, FlightData data) {
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
}