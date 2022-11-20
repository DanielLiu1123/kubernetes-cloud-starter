package com.freemanan.kubernetes.config.util;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.core.ConfigMapKey;
import java.util.Optional;

/**
 * @author Freeman
 */
public final class Util {

    private Util() {
        throw new AssertionError("No Util instances for you!");
    }

    public static ConfigMapKey configMapKey(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return new ConfigMapKey(
                configMap.getName(), namespace(configMap, properties), refreshable(configMap, properties));
    }

    public static String namespace(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return Optional.ofNullable(configMap.getNamespace()).orElseGet(properties::getDefaultNamespace);
    }

    public static boolean refreshable(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return Optional.ofNullable(configMap.getRefreshEnabled()).orElseGet(properties::isRefreshEnabled);
    }
}
