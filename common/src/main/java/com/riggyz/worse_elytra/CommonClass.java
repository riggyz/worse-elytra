package com.riggyz.worse_elytra;

import com.riggyz.worse_elytra.platform.Services;

public class CommonClass {
    // TODO: we need to overwrite the vanilla elytra here I think
    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!",
                Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        if (Services.PLATFORM.isModLoaded("worse_elytra")) {
            Constants.LOG.info("Hello to worse_elytra");
        }
    }
}