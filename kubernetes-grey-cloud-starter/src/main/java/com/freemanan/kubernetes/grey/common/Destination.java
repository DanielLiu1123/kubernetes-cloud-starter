package com.freemanan.kubernetes.grey.common;

import com.freemanan.kubernetes.commons.K8s;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Freeman
 */
@Getter
@Setter
@ToString
public class Destination {
    private String service;
    /**
     * Service namespace.
     * <p> If not in Kubernetes, use kubeconfig current context namespace.
     * <p> If in Kubernetes, use pod namespace.
     */
    private String namespace = K8s.currentNamespace();
    /**
     * Service port.
     * <p> Default value is null, means forwarding requests for all ports.
     */
    private Integer port;

    private Double weight;

    // We only need to compare service, namespace and port.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return Objects.equals(service, that.service)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, namespace, port);
    }

    public void validate() {
        if (service == null || service.trim().isEmpty()) {
            throw new IllegalArgumentException("service must be set !");
        }
        if (namespace == null) {
            throw new IllegalArgumentException("namespace must be set !");
        }
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("weight must be greater or equal than 0 !");
        }
        if (port != null && port < 0) {
            throw new IllegalArgumentException("port must be greater or equal than 0 !");
        }
    }
}
