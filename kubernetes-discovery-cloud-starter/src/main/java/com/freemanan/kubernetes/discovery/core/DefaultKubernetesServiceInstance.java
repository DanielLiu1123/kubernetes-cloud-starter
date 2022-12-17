/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freemanan.kubernetes.discovery.core;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author Freeman
 */
public final class DefaultKubernetesServiceInstance implements KubernetesServiceInstance {
    private final String instanceId;
    private final String serviceId;
    private final String host;
    private final int port;
    private final Map<String, String> metadata;
    private final boolean secure;
    private final String namespace;

    /**
     * @param instanceId the id of the instance.
     * @param serviceId  the id of the service.
     * @param host       the address where the service instance can be found.
     * @param port       the port on which the service is running.
     * @param metadata   a map containing metadata.
     * @param secure     indicates whether the connection needs to be secure.
     * @param namespace  the namespace of the service.
     */
    public DefaultKubernetesServiceInstance(
            String instanceId,
            String serviceId,
            String host,
            int port,
            Map<String, String> metadata,
            boolean secure,
            String namespace) {
        this.instanceId = instanceId;
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.metadata = metadata;
        this.secure = secure;
        this.namespace = namespace;
    }

    /**
     * @param instanceId the id of the instance.
     * @param serviceId  the id of the service.
     * @param host       the address where the service instance can be found.
     * @param port       the port on which the service is running.
     * @param metadata   a map containing metadata.
     * @param secure     indicates whether the connection needs to be secure.
     */
    public DefaultKubernetesServiceInstance(
            String instanceId, String serviceId, String host, int port, Map<String, String> metadata, boolean secure) {
        this(instanceId, serviceId, host, port, metadata, secure, null);
    }

    @Override
    public String getInstanceId() {
        return this.instanceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public URI getUri() {
        return createUri(isSecure() ? "https" : "http", host, port);
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String getScheme() {
        return isSecure() ? "https" : "http";
    }

    @Override
    public String getNamespace() {
        return namespace != null ? namespace : this.metadata.get("namespace");
    }

    private URI createUri(String scheme, String host, int port) {
        return URI.create(scheme + "://" + host + ":" + port);
    }

    public String instanceId() {
        return instanceId;
    }

    public String serviceId() {
        return serviceId;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public Map<String, String> metadata() {
        return metadata;
    }

    public boolean secure() {
        return secure;
    }

    public String namespace() {
        return namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultKubernetesServiceInstance that = (DefaultKubernetesServiceInstance) o;
        return port == that.port
                && secure == that.secure
                && Objects.equals(instanceId, that.instanceId)
                && Objects.equals(serviceId, that.serviceId)
                && Objects.equals(host, that.host)
                && Objects.equals(metadata, that.metadata)
                && Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, serviceId, host, port, metadata, secure, namespace);
    }

    @Override
    public String toString() {
        return "DefaultKubernetesServiceInstance{" + "instanceId='"
                + instanceId + '\'' + ", serviceId='"
                + serviceId + '\'' + ", host='"
                + host + '\'' + ", port="
                + port + ", metadata="
                + metadata + ", secure="
                + secure + ", namespace='"
                + namespace + '\'' + '}';
    }
}
