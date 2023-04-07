package com.freemanan.kubernetes.config;

import com.freemanan.kubernetes.commons.K8s;
import com.freemanan.kubernetes.config.util.ConfigPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private String namespace = determineNamespace();

    /**
     * Config preference, default is {@link ConfigPreference#REMOTE}, means remote configurations 'win', will override the local configurations.
     */
    private ConfigPreference preference = ConfigPreference.REMOTE;

    /**
     * Whether to refresh environment when remote resource was deleted, default value is {@code false}.
     * <p> The default value is {@code false} to prevent app arises abnormal situation from resource being deleted by mistake.
     */
    private boolean refreshOnDelete = false;

    /**
     * Whether to fail when the config (configmap/secret) is missing, default value is {@code true}.
     * <p> The default value is true to prevent unintended problems caused by not synchronizing the configuration between environments.
     */
    private boolean failOnMissingConfig = true;

    private List<ConfigMap> configMaps = new ArrayList<>();

    private List<Secret> secrets = new ArrayList<>();

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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    public List<Secret> getSecrets() {
        return secrets;
    }

    public void setSecrets(List<Secret> secrets) {
        this.secrets = secrets;
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

    public boolean isFailOnMissingConfig() {
        return failOnMissingConfig;
    }

    public void setFailOnMissingConfig(boolean failOnMissingConfig) {
        this.failOnMissingConfig = failOnMissingConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KubernetesConfigProperties that = (KubernetesConfigProperties) o;
        return enabled == that.enabled
                && refreshOnDelete == that.refreshOnDelete
                && failOnMissingConfig == that.failOnMissingConfig
                && refreshEnabled == that.refreshEnabled
                && Objects.equals(namespace, that.namespace)
                && preference == that.preference
                && Objects.equals(configMaps, that.configMaps)
                && Objects.equals(secrets, that.secrets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                enabled,
                namespace,
                preference,
                refreshOnDelete,
                failOnMissingConfig,
                configMaps,
                secrets,
                refreshEnabled);
    }

    @Override
    public String toString() {
        return "KubernetesConfigProperties{" + "enabled="
                + enabled + ", namespace='"
                + namespace + '\'' + ", preference="
                + preference + ", refreshOnDelete="
                + refreshOnDelete + ", failOnMissingConfig="
                + failOnMissingConfig + ", configMaps="
                + configMaps + ", secrets="
                + secrets + ", refreshEnabled="
                + refreshEnabled + '}';
    }

    public static class ConfigMap {
        /**
         * ConfigMap name.
         */
        private String name;
        /**
         * Namespace, using {@link KubernetesConfigProperties#namespace} if not set.
         */
        private String namespace;
        /**
         * Whether to enable the auto refresh on current ConfigMap, using {@link KubernetesConfigProperties#refreshEnabled} if not set.
         */
        private Boolean refreshEnabled;
        /**
         * Config preference, using {@link KubernetesConfigProperties#preference} if not set.
         */
        private ConfigPreference preference;

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

        public ConfigPreference getPreference() {
            return preference;
        }

        public void setPreference(ConfigPreference preference) {
            this.preference = preference;
        }

        @Override
        public String toString() {
            return "ConfigMap{" + "name='"
                    + name + '\'' + ", namespace='"
                    + namespace + '\'' + ", refreshEnabled="
                    + refreshEnabled + ", preference="
                    + preference + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConfigMap configMap = (ConfigMap) o;
            return Objects.equals(name, configMap.name)
                    && Objects.equals(namespace, configMap.namespace)
                    && Objects.equals(refreshEnabled, configMap.refreshEnabled)
                    && preference == configMap.preference;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, namespace, refreshEnabled, preference);
        }
    }

    public static class Secret {
        /**
         * Secret name.
         */
        private String name;
        /**
         * Namespace, using {@link KubernetesConfigProperties#namespace} if not set.
         */
        private String namespace;
        /**
         * Whether to enable the auto refresh on current Secret, default value is {@code false}.
         * <p> Use {@link KubernetesConfigProperties#refreshEnabled} if not set.
         */
        private Boolean refreshEnabled = false;
        /**
         * Config preference, using {@link KubernetesConfigProperties#preference} if not set.
         */
        private ConfigPreference preference;

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

        public ConfigPreference getPreference() {
            return preference;
        }

        public void setPreference(ConfigPreference preference) {
            this.preference = preference;
        }

        @Override
        public String toString() {
            return "Secret{" + "name='"
                    + name + '\'' + ", namespace='"
                    + namespace + '\'' + ", refreshEnabled="
                    + refreshEnabled + ", preference="
                    + preference + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Secret secret = (Secret) o;
            return Objects.equals(name, secret.name)
                    && Objects.equals(namespace, secret.namespace)
                    && Objects.equals(refreshEnabled, secret.refreshEnabled)
                    && preference == secret.preference;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, namespace, refreshEnabled, preference);
        }
    }

    private static String determineNamespace() {
        return K8s.currentNamespace();
    }
}
