package com.freemanan.kubernetes.discovery;

import com.freemanan.kubernetes.commons.KubernetesClientConfiguration;
import com.freemanan.kubernetes.discovery.core.KubernetesDiscoveryClient;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Freeman
 */
@AutoConfiguration
@ConditionalOnClass({Service.class, Pod.class})
@ConditionalOnProperty(prefix = KubernetesDiscoveryProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(KubernetesDiscoveryProperties.class)
@Import(KubernetesClientConfiguration.class)
public class KubernetesDiscoveryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KubernetesDiscoveryClient kubernetesDiscoveryClient(
            KubernetesClient kubernetesClient, KubernetesDiscoveryProperties properties) {
        return new KubernetesDiscoveryClient(kubernetesClient, properties);
    }
}
