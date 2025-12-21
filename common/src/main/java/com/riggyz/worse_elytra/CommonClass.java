package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.platform.Services;

/**
 * The generic mod class that is used to implement anything that can be generic
 * between the two modloaders.
 * 
 * @see WorseElytra
 */
public class CommonClass {

    /**
     * Universal init function that is called by both modloaders.
     * 
     * Note that this is called by the client+server for both modloaders, so
     * anything specific to the client will error out here.
     */
    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!",
                Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        if (Services.PLATFORM.isModLoaded("worse_elytra")) {
            Constants.LOG.info("Hello to worse_elytra");
        }
    }
}