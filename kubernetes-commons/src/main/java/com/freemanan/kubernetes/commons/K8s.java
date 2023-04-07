package com.freemanan.kubernetes.commons;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * @author Freeman
 */
public final class K8s {

    private K8s() {
        throw new UnsupportedOperationException("No K8s instances for you!");
    }

    private static final Config config = new ConfigBuilder().build();

    public static Config config() {
        return config;
    }

    public static String currentNamespace() {
        String namespace = config.getNamespace();
        return namespace != null ? namespace : "default";
    }

    /**
     * New a KubernetesClient instance.
     *
     * @return new KubernetesClient instance
     */
    public static KubernetesClient newKubernetesClient() {
        return new KubernetesClientBuilder().withConfig(config).build();
    }
}
