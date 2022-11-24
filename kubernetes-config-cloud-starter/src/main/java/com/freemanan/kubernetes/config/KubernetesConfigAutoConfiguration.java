package com.freemanan.kubernetes.config;

import com.freemanan.kubernetes.config.core.ConfigMapWatcher;
import com.freemanan.kubernetes.config.util.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({KubernetesClient.class, ConfigMap.class})
@ConditionalOnProperty(prefix = KubernetesConfigProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KubernetesConfigProperties.class)
public class KubernetesConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KubernetesClient fabric8KubernetesClient() {
        return KubernetesUtil.kubernetesClient();
    }

    @Bean
    public ConfigMapWatcher configMapWatcher(KubernetesConfigProperties properties, KubernetesClient kubernetesClient) {
        return new ConfigMapWatcher(properties, kubernetesClient);
    }
}
