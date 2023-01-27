package com.freemanan.kubernetes.discovery;

import com.freemanan.kubernetes.commons.KubernetesClientConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Kubernetes discovery client integration tests.
 *
 * @author Freeman
 */
@Disabled("Still in development")
class DiscoveryIntegrationTests {

    @Test
    void testDiscoveryClient() {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Config.class)
                .properties(KubernetesDiscoveryProperties.PREFIX + ".namespaces[0]=*")
                .properties(KubernetesDiscoveryProperties.PREFIX + ".patterns[0]=istio.*")
                .run();

        DiscoveryClient discoveryClient = ctx.getBean(DiscoveryClient.class);
        discoveryClient.getServices().forEach(System.out::println);

        discoveryClient.getInstances("istiod").forEach(System.out::println);

        discoveryClient.getInstances("istiod2").forEach(System.out::println);

        ctx.close();
    }

    @SpringBootApplication
    @ImportAutoConfiguration(KubernetesClientConfiguration.class)
    static class Config {}
}
