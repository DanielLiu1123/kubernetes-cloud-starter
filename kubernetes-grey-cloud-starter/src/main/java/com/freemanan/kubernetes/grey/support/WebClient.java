package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.client.webclient.GreyWebClientCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebClientCustomizer.class)
public class WebClient {

    @Bean
    @ConditionalOnMissingBean
    public GreyWebClientCustomizer greyWebClientCustomizer() {
        return new GreyWebClientCustomizer();
    }
}
