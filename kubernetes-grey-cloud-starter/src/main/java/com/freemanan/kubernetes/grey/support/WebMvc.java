package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.server.webmvc.GreyOncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvc {

    @Bean
    @ConditionalOnMissingBean
    public GreyOncePerRequestFilter greyOncePerRequestFilter() {
        return new GreyOncePerRequestFilter();
    }
}
