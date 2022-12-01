package com.freemanan.kubernetes.config.util;

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

    public static synchronized KubernetesClient getKubernetesClient() {
        KubernetesClient client = kubernetesClient.get();
        if (client == null) {
            kubernetesClient.set(KubernetesUtil.newKubernetesClient());
        }
        return kubernetesClient.get();
    }

    public static synchronized void remove() {
        kubernetesClient.set(null);
    }
}
