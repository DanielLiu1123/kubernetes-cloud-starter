package com.freemanan.kubernetes.config;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.freemanan.kubernetes.config.core.ConfigMissingException;
import com.freemanan.kubernetes.config.testsupport.KubernetesAvailable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Freeman
 */
@KubernetesAvailable
public class MissingConfigIntegrationTests {

    @Test
    void testEnabledFailOnMissingConfig() {
        assertThatCode(() -> new SpringApplicationBuilder(Empty.class)
                        .web(WebApplicationType.NONE)
                        .profiles("missing-config")
                        .run())
                .isInstanceOf(ConfigMissingException.class);
    }

    @Test
    void testDisabledFailOnMissingConfig() {
        assertThatCode(() -> new SpringApplicationBuilder(Empty.class)
                        .web(WebApplicationType.NONE)
                        .properties(KubernetesConfigProperties.PREFIX + ".fail-on-missing-config=false")
                        .profiles("missing-config")
                        .run())
                .doesNotThrowAnyException();
    }
}
