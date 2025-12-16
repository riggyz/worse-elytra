package com.riggyz.modbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "modbox";
	public static final String MOD_NAME = "Balanced Elytra";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	// Elytra related constants
	public static final String CUSTOM_ELYTRA_ID = "custom_elytra";
	public static final int ELYTRA_BASE_DURABILITY = 324;
	public static final String ELYTRA_NORMAL_LORE_KEY = "lore.modbox.normal";

	public static final String ELYTRA_RUFFLED_PREFIX_KEY = "item.modbox.ruffled_prefix";
	public static final String ELYTRA_RUFFLED_LORE_KEY = "lore.modbox.ruffled";

	public static final String ELYTRA_WITHERED_PREFIX_KEY = "item.modbox.withered_prefix";
	public static final String ELYTRA_WITHERED_LORE_KEY = "lore.modbox.withered";

	public static final String ELYTRA_BROKEN_PREFIX_KEY = "item.modbox.broken_prefix";
	public static final String ELYTRA_BROKEN_LORE_KEY = "lore.modbox.broken";
}