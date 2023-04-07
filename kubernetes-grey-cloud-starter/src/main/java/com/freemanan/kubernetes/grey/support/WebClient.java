package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.client.webclient.GreyWebClientCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({WebClientCustomizer.class, ExchangeFilterFunction.class})
public class WebClient {

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public GreyWebClientCustomizer greyWebClientCustomizer() {
        return new GreyWebClientCustomizer();
    }
}
