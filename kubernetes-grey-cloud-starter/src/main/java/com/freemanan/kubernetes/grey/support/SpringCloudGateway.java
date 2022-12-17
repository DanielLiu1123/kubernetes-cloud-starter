package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.KubernetesGreyProperties;
import com.freemanan.kubernetes.grey.server.scg.GreyGlobalFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(GlobalFilter.class)
public class SpringCloudGateway {

    @Bean
    public GreyGlobalFilter greyGlobalFilter(KubernetesGreyProperties properties) {
        return new GreyGlobalFilter(properties);
    }
}
