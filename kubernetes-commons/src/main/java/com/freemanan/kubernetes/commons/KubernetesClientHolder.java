package com.freemanan.kubernetes.commons;

import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.concurrent.atomic.AtomicReference;

/**
 * KubernetesClient holder, we need to ensure that only one KubernetesClient instance in one application.
 *
 * <p> Note: there's only one KubernetesClient instance in <strong>one application</strong>, but one JVM may have multiple instances, because the tests need to use multiple instances.
 *
 * @author Freeman
 */
public class KubernetesClientHolder {

    private static final AtomicReference<KubernetesClient> kubernetesClient = new AtomicReference<>();

    public static KubernetesClient getKubernetesClient() {
        KubernetesClient client = kubernetesClient.get();
        if (client != null) {
            return client;
        }
        kubernetesClient.compareAndSet(null, K8s.newKubernetesClient());
        return kubernetesClient.get();
    }

    public static void remove() {
        kubernetesClient.set(null);
    }
}
