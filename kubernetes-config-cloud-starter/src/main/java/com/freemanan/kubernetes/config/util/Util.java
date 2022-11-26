package com.freemanan.kubernetes.config.util;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.core.ResourceKey;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import java.util.Optional;

/**
 * @author Freeman
 */
public final class Util {

    private Util() {
        throw new UnsupportedOperationException("No Util instances for you!");
    }

    public static ResourceKey resourceKey(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return new ResourceKey(
                ConfigMap.class.getSimpleName(),
                configMap.getName(),
                namespace(configMap, properties),
                refreshable(configMap, properties));
    }

    public static ResourceKey resourceKey(
            KubernetesConfigProperties.Secret secret, KubernetesConfigProperties properties) {
        return new ResourceKey(
                Secret.class.getSimpleName(),
                secret.getName(),
                namespace(secret, properties),
                refreshable(secret, properties));
    }

    public static String namespace(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return Optional.ofNullable(configMap.getNamespace()).orElseGet(properties::getNamespace);
    }

    public static String namespace(KubernetesConfigProperties.Secret secret, KubernetesConfigProperties properties) {
        return Optional.ofNullable(secret.getNamespace()).orElseGet(properties::getNamespace);
    }

    public static boolean refreshable(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return Optional.ofNullable(configMap.getRefreshEnabled()).orElseGet(properties::isRefreshEnabled);
    }

    public static boolean refreshable(KubernetesConfigProperties.Secret secret, KubernetesConfigProperties properties) {
        return Optional.ofNullable(secret.getRefreshEnabled()).orElseGet(properties::isRefreshEnabled);
    }

    public static ConfigPreference preference(
            KubernetesConfigProperties.ConfigMap configMap, KubernetesConfigProperties properties) {
        return Optional.ofNullable(configMap.getPreference()).orElseGet(properties::getPreference);
    }

    public static ConfigPreference preference(
            KubernetesConfigProperties.Secret secret, KubernetesConfigProperties properties) {
        return Optional.ofNullable(secret.getPreference()).orElseGet(properties::getPreference);
    }
}
