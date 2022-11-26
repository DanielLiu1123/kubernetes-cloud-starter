package com.freemanan.kubernetes.config.util;

import com.freemanan.kubernetes.config.core.ResourceKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Freeman
 */
public final class Exister {

    private Exister() {
        throw new UnsupportedOperationException("No Exister instances for you!");
    }

    private static final ConcurrentMap<ResourceKey, Boolean> CACHE = new ConcurrentHashMap<>();

    /**
     * Whether the resource exists when prepare the environment
     *
     * @param resourceKey {@link ResourceKey}
     * @return true if the resource exists when prepare the environment
     */
    public static boolean existWhenPrepareEnvironment(ResourceKey resourceKey) {
        return CACHE.getOrDefault(resourceKey, true);
    }

    /**
     * Mark the resource not exists when prepare the environment.
     *
     * @param resourceKey {@link ResourceKey}
     */
    public static void markNotExistWhenPrepareEnvironment(ResourceKey resourceKey) {
        CACHE.put(resourceKey, false);
    }

    /**
     * Clear all existence mark.
     */
    public static void clean() {
        CACHE.clear();
    }
}
