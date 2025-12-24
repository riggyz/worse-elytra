package com.riggyz.worse_elytra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Universal constants used all throughout the mod. Defined here so that there
 * is a single point of modification for magic numbers and strings.
 */
public class Constants {

	// NOTE: Meta constants
	/** Constant for the mod id */
	public static final String MOD_ID = "worse_elytra";
	/** Constant for the mod name */
	public static final String MOD_NAME = "Worse Elytra";
	/** Constant for the mod specific logger */
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	/** OP level required to run the commands */
	public static final int OP_LEVEL = 2;

	// NOTE: Elytra mechanic related constants
	/** The maximum durability an elytra can have */
	public static final int ELYTRA_BASE_DURABILITY = 324;
	/** The nbt key for all the state information */
	public static final String ELYTRA_NBT_KEY = "elytra_state";

	/** Amount of items needed to completely repair durability for a given state */
	public static final int DURABILITY_REPAIR_COST = 4;
	/** XP cost of the membrane repair item */
	public static final int XP_PER_DURABILITY_MEMBRANE = 1;
	/** XP cost of the membrane state repair item */
	public static final int XP_PER_UPGRADE_MEMBRANE = 1;

	// NOTE: Elytra visual related constants
	/** Lore key for the normal elytra */
	public static final String ELYTRA_NORMAL_LORE_KEY = "lore.worse_elytra.normal";
	/** Lore key for the ruffled elytra */
	public static final String ELYTRA_RUFFLED_LORE_KEY = "lore.worse_elytra.ruffled";
	/** Lore key for the withered elytra */
	public static final String ELYTRA_WITHERED_LORE_KEY = "lore.worse_elytra.withered";
	/** Lore key for the broken elytra */
	public static final String ELYTRA_BROKEN_LORE_KEY = "lore.worse_elytra.broken";

	/** Prefix key for the ruffled elytra */
	public static final String ELYTRA_RUFFLED_PREFIX_KEY = "item.worse_elytra.ruffled_prefix";
	/** Prefix key for the withered elytra */
	public static final String ELYTRA_WITHERED_PREFIX_KEY = "item.worse_elytra.withered_prefix";
	/** Prefix key for the broken elytra */
	public static final String ELYTRA_BROKEN_PREFIX_KEY = "item.worse_elytra.broken_prefix";
}