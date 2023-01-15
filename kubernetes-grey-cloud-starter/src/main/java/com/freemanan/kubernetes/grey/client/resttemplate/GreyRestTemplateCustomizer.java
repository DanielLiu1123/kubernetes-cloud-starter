package com.freemanan.kubernetes.grey.client.resttemplate;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

/**
 * @author Freeman
 */
public class GreyRestTemplateCustomizer implements RestTemplateCustomizer {
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new GreyClientHttpRequestInterceptor());
    }
}
