package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.server.webflux.GreyWebFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFlux {

    @Bean
    @ConditionalOnMissingBean
    public GreyWebFilter greyWebFilter() {
        return new GreyWebFilter();
    }
}
