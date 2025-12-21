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

	// NOTE: Elytra mechanic related constants
	/** The maximum durability an elytra can have */
	public static final int ELYTRA_BASE_DURABILITY = 324;

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