package com.riggyz.worse_elytra.platform;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * This is some multiloader magic, no idea what it does.
 */
public class Services {
    /** Modloader specific platform class loading */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Function that loads the specific platform service depending on which
     * modloader context we are in.
     * 
     * @param <T>   interface to return as
     * @param clazz parent class to load
     * 
     * @return class that implements T
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}