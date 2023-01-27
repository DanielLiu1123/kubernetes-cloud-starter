package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.client.feign.GreyFeignBuilderCustomizer;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Feign.class, FeignBuilderCustomizer.class})
public class OpenFeign {

    @Bean
    @ConditionalOnMissingBean
    public GreyFeignBuilderCustomizer greyFeignBuilderCustomizer() {
        return new GreyFeignBuilderCustomizer();
    }
}
