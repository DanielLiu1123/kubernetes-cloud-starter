package com.freemanan.kubernetes.config.core;

import java.util.Objects;

/**
 * @author Freeman
 */
public class ConfigMapKey {
    private final String name;
    private final String namespace;
    private final boolean refreshEnabled;

    public ConfigMapKey(String name, String namespace, boolean refreshEnabled) {
        this.name = name;
        this.namespace = namespace;
        this.refreshEnabled = refreshEnabled;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigMapKey that = (ConfigMapKey) o;
        return refreshEnabled == that.refreshEnabled
                && Objects.equals(name, that.name)
                && Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, refreshEnabled);
    }

    @Override
    public String toString() {
        return "ConfigMapKey{" + "name='"
                + name + '\'' + ", namespace='"
                + namespace + '\'' + ", refreshEnabled="
                + refreshEnabled + '}';
    }
}
