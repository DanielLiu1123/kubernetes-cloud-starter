package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.client.feign.GreyRequestInterceptor;
import com.freemanan.kubernetes.grey.client.feign.GreyTargeter;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Freeman
 * @since 2022/12/18
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Feign.class, FeignClientFactoryBean.class})
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class OpenFeign {

    @Bean
    @ConditionalOnMissingBean
    public Targeter greyTargeter() {
        return new GreyTargeter();
    }

    @Bean
    public GreyRequestInterceptor greyRequestInterceptor() {
        return new GreyRequestInterceptor();
    }
}
