package com.freemanan.kubernetes.grey.common;

import com.freemanan.kubernetes.commons.KubernetesUtil;
import java.util.Objects;

/**
 * @author Freeman
 */
public class Destination {
    private String service;
    /**
     * Service namespace.
     * <p> If not in Kubernetes, use kubeconfig current context namespace.
     * <p> If in Kubernetes, use pod namespace.
     */
    private String namespace = KubernetesUtil.currentNamespace();
    /**
     * Service port.
     * <p> Default value is null, means forwarding requests for all ports.
     */
    private Integer port;

    private Double weight;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

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

    @Override
    public String toString() {
        return "Destination{" + "service='"
                + service + '\'' + ", namespace='"
                + namespace + '\'' + ", port="
                + port + ", weight="
                + weight + '}';
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
