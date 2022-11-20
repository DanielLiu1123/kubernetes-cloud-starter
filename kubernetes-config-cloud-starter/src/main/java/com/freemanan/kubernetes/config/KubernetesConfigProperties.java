package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.util.KubernetesUtil.currentNamespace;

import com.freemanan.kubernetes.config.util.ConfigPreference;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 */
@Data
@ConfigurationProperties(KubernetesConfigProperties.PREFIX)
public class KubernetesConfigProperties {
    public static final String PREFIX = "microservice-base.kubernetes.config";

    private boolean enabled = true;

    /**
     * Default namespace.
     * <p> 1. If in Kubernetes environment, use the namespace of the current pod.
     * <p> 2. If not in Kubernetes environment, use the namespace of the current context.
     */
    private String defaultNamespace = currentNamespace();

    /**
     * Config preference, default is {@link ConfigPreference#LOCAL}, means local configurations 'win', will override the remote configurations.
     */
    private ConfigPreference preference = ConfigPreference.LOCAL;

    private List<ConfigMap> configMaps = new ArrayList<>();

    /**
     * Whether to enable the auto refresh feature.
     */
    private boolean refreshEnabled = true;

    @Data
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
    }
}
