package com.riggyz.worse_elytra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Universal constants used all throughout the mod. Defined here so that there
 * is a single point of modification for magic numbers and strings.
 */
public class Constants {
	public static final String MOD_ID = "worse_elytra";
	public static final String MOD_NAME = "Worse Elytra";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	// Elytra related constants
	public static final String CUSTOM_ELYTRA_ID = "custom_elytra";
	public static final int ELYTRA_BASE_DURABILITY = 324;
	public static final String ELYTRA_NORMAL_LORE_KEY = "lore.worse_elytra.normal";

	public static final String ELYTRA_RUFFLED_PREFIX_KEY = "item.worse_elytra.ruffled_prefix";
	public static final String ELYTRA_RUFFLED_LORE_KEY = "lore.worse_elytra.ruffled";

	public static final String ELYTRA_WITHERED_PREFIX_KEY = "item.worse_elytra.withered_prefix";
	public static final String ELYTRA_WITHERED_LORE_KEY = "lore.worse_elytra.withered";

	public static final String ELYTRA_BROKEN_PREFIX_KEY = "item.worse_elytra.broken_prefix";
	public static final String ELYTRA_BROKEN_LORE_KEY = "lore.worse_elytra.broken";
}