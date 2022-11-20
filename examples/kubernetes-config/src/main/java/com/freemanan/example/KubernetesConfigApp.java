package com.freemanan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Freeman
 */
@SpringBootApplication
@EnableConfigurationProperties(PricingProperties.class)
public class KubernetesConfigApp {
    public static void main(String[] args) {
        SpringApplication.run(KubernetesConfigApp.class, args);
    }
}
