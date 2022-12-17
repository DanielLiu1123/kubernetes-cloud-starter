package com.freemanan.kubernetes.commons;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Optional;

/**
 * @author Freeman
 */
public final class KubernetesUtil {

    private KubernetesUtil() {
        throw new UnsupportedOperationException("No KubernetesUtil instances for you!");
    }

    private static final Config config = new ConfigBuilder().build();

    public static Config config() {
        return config;
    }

    public static String currentNamespace() {
        return Optional.ofNullable(config.getNamespace()).orElse("default");
    }

    /**
     * New a KubernetesClient instance.
     *
     * @return new KubernetesClient instance
     */
    public static KubernetesClient newKubernetesClient() {
        return new DefaultKubernetesClient(config);
    }
}
