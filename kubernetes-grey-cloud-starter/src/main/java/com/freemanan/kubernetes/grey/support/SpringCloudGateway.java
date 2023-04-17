package com.freemanan.kubernetes.grey.support;

import com.freemanan.kubernetes.grey.GreyApi;
import com.freemanan.kubernetes.grey.KubernetesGreyProperties;
import com.freemanan.kubernetes.grey.server.scg.GreyGlobalFilter;
import java.net.URI;
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
    @ConditionalOnMissingBean
    public GreyGlobalFilter greyGlobalFilter(WebClient.Builder builder, KubernetesGreyProperties properties) {
        String authority = properties.getGreyGateway().getAuthority();
        String baseUrl = authority.contains("://") ? authority : "http://" + authority;
        WebClient webclient = builder.baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .clientAdapter(WebClientAdapter.forClient(webclient))
                .build();
        GreyApi greyApi = factory.createClient(GreyApi.class);
        String url = baseUrl + properties.getGreyGateway().getPath();
        return new GreyGlobalFilter(greyApi, URI.create(url));
    }
}
