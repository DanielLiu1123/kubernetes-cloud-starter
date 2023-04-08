package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.GreyApi;
import com.freemanan.kubernetes.grey.KubernetesGreyProperties;
import com.freemanan.kubernetes.grey.server.scg.GreyGlobalFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(GlobalFilter.class)
public class SpringCloudGateway {

    @Bean
    public GreyApi greyApi(WebClient.Builder builder) {
        WebClient webclient = builder.baseUrl("http://localhost:8080").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .clientAdapter(WebClientAdapter.forClient(webclient))
                .build();
        return factory.createClient(GreyApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public GreyGlobalFilter greyGlobalFilter(KubernetesGreyProperties properties, GreyApi greyApi) {
        return new GreyGlobalFilter(properties, greyApi);
    }
}
