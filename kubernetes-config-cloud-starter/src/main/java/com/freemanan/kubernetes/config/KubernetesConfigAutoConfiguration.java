package com.freemanan.kubernetes.config;

import com.freemanan.kubernetes.config.core.ConfigWatcher;
import com.freemanan.kubernetes.config.util.KubernetesClientHolder;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Freeman
 */
@AutoConfiguration
@ConditionalOnClass({KubernetesClient.class, ConfigMap.class})
@ConditionalOnProperty(prefix = KubernetesConfigProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KubernetesConfigProperties.class)
public class KubernetesConfigAutoConfiguration implements DisposableBean {

    @Bean
    @ConditionalOnMissingBean
    public KubernetesClient fabric8KubernetesClient() {
        return KubernetesClientHolder.getKubernetesClient();
    }

    @Bean
    public ConfigWatcher configWatcher(KubernetesConfigProperties properties, KubernetesClient kubernetesClient) {
        return new ConfigWatcher(properties, kubernetesClient);
    }

    @Override
    public void destroy() throws Exception {
        KubernetesClientHolder.remove();
    }
}
