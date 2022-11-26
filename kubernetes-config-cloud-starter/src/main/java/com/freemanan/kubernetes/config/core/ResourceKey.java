package com.freemanan.kubernetes.config.core;

import java.util.Objects;

/**
 * @author Freeman
 */
public class ResourceKey {
    private final String type;
    private final String name;
    private final String namespace;
    private final boolean refreshEnabled;

    public ResourceKey(String type, String name, String namespace, boolean refreshEnabled) {
        this.type = type;
        this.name = name;
        this.namespace = namespace;
        this.refreshEnabled = refreshEnabled;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    @Override
    public String toString() {
        return "ResourceKey{" + "type='"
                + type + '\'' + ", name='"
                + name + '\'' + ", namespace='"
                + namespace + '\'' + ", refreshEnabled="
                + refreshEnabled + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceKey that = (ResourceKey) o;
        return refreshEnabled == that.refreshEnabled
                && Objects.equals(type, that.type)
                && Objects.equals(name, that.name)
                && Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, namespace, refreshEnabled);
    }
}
