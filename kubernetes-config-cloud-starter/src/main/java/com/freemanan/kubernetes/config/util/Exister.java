package com.freemanan.kubernetes.config.util;

import com.freemanan.kubernetes.config.core.ConfigMapKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Freeman
 */
public final class Exister {

    private Exister() {
        throw new UnsupportedOperationException("No Exister instances for you!");
    }

    private static final ConcurrentMap<ConfigMapKey, Boolean> CACHE = new ConcurrentHashMap<>();

    public static boolean existWhenAppStartup(ConfigMapKey configMap) {
        return CACHE.getOrDefault(configMap, true);
    }

    public static void markNotExistWhenAppStartup(ConfigMapKey configMap) {
        CACHE.put(configMap, false);
    }

    public static void clean() {
        CACHE.clear();
    }
}
