package com.freemanan.kubernetes.commons;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 * @since 2022/12/16
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KubernetesClient.class)
public class KubernetesClientAutoConfiguration implements DisposableBean {

    @Bean
    @ConditionalOnMissingBean
    public KubernetesClient fabric8KubernetesClient() {
        return KubernetesClientHolder.getKubernetesClient();
    }

    @Override
    public void destroy() throws Exception {
        KubernetesClientHolder.remove();
    }
}
