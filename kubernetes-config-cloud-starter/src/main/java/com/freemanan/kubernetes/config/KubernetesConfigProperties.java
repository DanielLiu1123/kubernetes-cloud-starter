package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.util.KubernetesUtil.currentNamespace;

import com.freemanan.kubernetes.config.util.ConfigPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 */
@ConfigurationProperties(KubernetesConfigProperties.PREFIX)
public class KubernetesConfigProperties {
    public static final String PREFIX = "microservice-base.kubernetes.config";

    private boolean enabled = true;

    /**
     * Default namespace.
     * <p> 1. If in Kubernetes environment, use the namespace of the current pod.
     * <p> 2. If not in Kubernetes environment, use the namespace of the current context.
     */
    private String defaultNamespace = determineNamespace();

    /**
     * Config preference, default is {@link ConfigPreference#REMOTE}, means remote configurations 'win', will override the local configurations.
     */
    private ConfigPreference preference = ConfigPreference.REMOTE;

    /**
     * Whether to refresh environment when remote resource was deleted, default value is {@code false}.
     * <p> The default value is {@code false} to prevent app arises abnormal situation from resource being deleted by mistake.
     */
    private boolean refreshOnDelete = false;

    private List<ConfigMap> configMaps = new ArrayList<>();

    /**
     * Whether to enable the auto refresh feature.
     */
    private boolean refreshEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public ConfigPreference getPreference() {
        return preference;
    }

    public void setPreference(ConfigPreference preference) {
        this.preference = preference;
    }

    public List<ConfigMap> getConfigMaps() {
        return configMaps;
    }

    public void setConfigMaps(List<ConfigMap> configMaps) {
        this.configMaps = configMaps;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public boolean isRefreshOnDelete() {
        return refreshOnDelete;
    }

    public void setRefreshOnDelete(boolean refreshOnDelete) {
        this.refreshOnDelete = refreshOnDelete;
    }

    @Override
    public String toString() {
        return "KubernetesConfigProperties{" + "enabled="
                + enabled + ", defaultNamespace='"
                + defaultNamespace + '\'' + ", preference="
                + preference + ", configMaps="
                + configMaps + ", refreshEnabled="
                + refreshEnabled + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KubernetesConfigProperties that = (KubernetesConfigProperties) o;
        return enabled == that.enabled
                && refreshOnDelete == that.refreshOnDelete
                && refreshEnabled == that.refreshEnabled
                && Objects.equals(defaultNamespace, that.defaultNamespace)
                && preference == that.preference
                && Objects.equals(configMaps, that.configMaps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, defaultNamespace, preference, refreshOnDelete, configMaps, refreshEnabled);
    }

    public static class ConfigMap {
        /**
         * ConfigMap name.
         */
        private String name;
        /**
         * Namespace, using {@link KubernetesConfigProperties#defaultNamespace} if not set.
         */
        private String namespace;
        /**
         * Whether to enable the auto refresh on current ConfigMap, using {@link KubernetesConfigProperties#refreshEnabled} if not set.
         */
        private Boolean refreshEnabled;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public Boolean getRefreshEnabled() {
            return refreshEnabled;
        }

        public void setRefreshEnabled(Boolean refreshEnabled) {
            this.refreshEnabled = refreshEnabled;
        }

        @Override
        public String toString() {
            return "ConfigMap{" + "name='"
                    + name + '\'' + ", namespace='"
                    + namespace + '\'' + ", refreshEnabled="
                    + refreshEnabled + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConfigMap configMap = (ConfigMap) o;
            return Objects.equals(name, configMap.name)
                    && Objects.equals(namespace, configMap.namespace)
                    && Objects.equals(refreshEnabled, configMap.refreshEnabled);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, namespace, refreshEnabled);
        }
    }

    private static String determineNamespace() {
        return Optional.ofNullable(currentNamespace()).orElse("default");
    }
}
