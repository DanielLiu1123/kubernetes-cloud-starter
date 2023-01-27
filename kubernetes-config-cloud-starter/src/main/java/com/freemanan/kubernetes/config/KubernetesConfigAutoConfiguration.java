package com.freemanan.kubernetes.config;

import com.freemanan.kubernetes.commons.KubernetesClientConfiguration;
import com.freemanan.kubernetes.config.core.ConfigWatcher;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Freeman
 */
@AutoConfiguration
@ConditionalOnClass({KubernetesClient.class, ConfigMap.class})
@ConditionalOnProperty(prefix = KubernetesConfigProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KubernetesConfigProperties.class)
@Import(KubernetesClientConfiguration.class)
public class KubernetesConfigAutoConfiguration {

    @Bean
    public ConfigWatcher configWatcher(KubernetesConfigProperties properties, KubernetesClient kubernetesClient) {
        return new ConfigWatcher(properties, kubernetesClient);
    }
}
