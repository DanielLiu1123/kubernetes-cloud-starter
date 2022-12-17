package com.freemanan.kubernetes.grey;

import com.freemanan.kubernetes.commons.KubernetesClientAutoConfiguration;
import com.freemanan.kubernetes.grey.support.OpenFeign;
import com.freemanan.kubernetes.grey.support.SpringCloudGateway;
import com.freemanan.kubernetes.grey.support.WebMvc;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KubernetesClient.class)
@ConditionalOnProperty(prefix = KubernetesGreyProperties.PREFIX, name = "enabled", matchIfMissing = true)
@AutoConfigureAfter(KubernetesClientAutoConfiguration.class)
@EnableConfigurationProperties(KubernetesGreyProperties.class)
public class KubernetesGreyAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Import({SpringCloudGateway.class, WebMvc.class})
    static class ServerSupport {}

    @Configuration(proxyBeanMethods = false)
    @Import({OpenFeign.class})
    static class ClientSupport {}
}
