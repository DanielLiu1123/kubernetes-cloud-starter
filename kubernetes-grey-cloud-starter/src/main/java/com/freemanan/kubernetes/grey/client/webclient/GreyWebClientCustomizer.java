package com.freemanan.kubernetes.grey.client.webclient;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Freeman
 */
public class GreyWebClientCustomizer implements WebClientCustomizer {
    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(new GreyExchangeFilterFunction());
    }
}
