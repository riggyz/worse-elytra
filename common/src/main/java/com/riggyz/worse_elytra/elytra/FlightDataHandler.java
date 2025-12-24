package com.riggyz.worse_elytra.elytra;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Class to hold parts of the flight data so that it can be accessed outside of
 * mixin context.
 */
public class FlightDataHandler {

    /** Runtime only map of players flight data */
    private static final Map<UUID, FlightData> FLIGHT_DATA = new WeakHashMap<>();
    /** Runtime only map of who has the hud enabled */
    private static final Map<UUID, Boolean> HUD_ENABLED = new WeakHashMap<>();

    /**
     * Flight data class that encapsulates some data that the mixin collects.
     */
    public static class FlightData {
        /** Last known flight position */
        public Vec3 lastPosition;
        /** Flight distance this session */
        public double totalDistance;
        /** Whether the player was flying */
        public boolean wasFlying;

        /**
         * Public consturctor for the Flightdata class.
         * 
         * @param startPos position to create class instance with
         */
        public FlightData(Vec3 startPos) {
            this.lastPosition = startPos;
            this.totalDistance = 0;
            this.wasFlying = true;
        }
    }

    /**
     * Public wrapper that toggles the HUD for a given player.
     * 
     * @param player entity to toggle hud for
     */
    public static void toggleDebugHUD(Player player) {
        UUID id = player.getUUID();
        boolean current = HUD_ENABLED.getOrDefault(id, false);
        HUD_ENABLED.put(id, !current);
    }

    // NOTE: Getters and Setters

    /**
     * Public getter for the flight data hashmap. Done to avoid mixin issues.
     * 
     * @param playerId player to check the hashmap for
     * 
     * @return a FlightData class
     */
    public static FlightData getFlightData(UUID playerId) {
        return FLIGHT_DATA.get(playerId);
    }

    /**
     * Public setter for the flight data hashmap. Done to avoid mixin issues.
     * 
     * @param playerId player to check the hashmap for
     * @param data     FlightData to set in the hashmap
     */
    public static void setFlightData(UUID playerId, FlightData data) {
        FLIGHT_DATA.put(playerId, data);
    }

    /**
     * Public deleter for the flight data hashmap. Done to avoid mixin issues.
     * 
     * @param playerId player to delete from the hashmap
     */
    public static void removeFlightData(UUID playerId) {
        FLIGHT_DATA.remove(playerId);
    }

    /**
     * Public getter for the hud data hashmap. Done to avoid mixin issues.
     * 
     * @param playerId player to check the hashmap for
     * 
     * @return true if enabled, false otherwise
     */
    public static Boolean getHudData(UUID playerId) {
        return HUD_ENABLED.getOrDefault(playerId, false);
    }

    /**
     * Public setter for the hud data hashmap. Done to avoid mixin issues.
     * 
     * @param playerId player to check the hashmap for
     * @param data     boolean to set in the hashmap
     */
    public static void setHudData(UUID playerId, Boolean data) {
        HUD_ENABLED.put(playerId, data);
    }
}
