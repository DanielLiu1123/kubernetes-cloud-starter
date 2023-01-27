package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.client.resttemplate.GreyRestTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RestTemplateCustomizer.class, ClientHttpRequestInterceptor.class})
public class RestTemplate {

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public GreyRestTemplateCustomizer greyRestTemplateCustomizer() {
        return new GreyRestTemplateCustomizer();
    }
}
