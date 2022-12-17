package com.freemanan.kubernetes.discovery.core;

import org.springframework.cloud.client.ServiceInstance;

/**
 * @author Freeman
 */
public interface KubernetesServiceInstance extends ServiceInstance {

    /**
     * @return the namespace of the service.
     */
    String getNamespace();
}
