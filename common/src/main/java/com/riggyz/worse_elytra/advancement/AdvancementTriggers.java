package com.riggyz.worse_elytra.advancement;

/**
 * Like ModItems, this is a wrapper class for all custom advancement triggers
 * implemented in this mod. Done this way so that any modloader can use a
 * central variable for advancements.
 */
public class AdvancementTriggers {

    /** Public handle for the elytra degrading trigger */
    public static final ElytraDegradedTrigger ELYTRA_DEGRADED = new ElytraDegradedTrigger();
}