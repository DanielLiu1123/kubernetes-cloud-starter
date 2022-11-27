package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceSecret;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteSecret;
import static org.assertj.core.api.Assertions.assertThat;

import com.freemanan.kubernetes.config.testsupport.KubernetesAvailable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Freeman
 */
@KubernetesAvailable
public class SecretIntegrationTests {

    static ConfigurableApplicationContext ctx;

    @BeforeAll
    static void init() {
        createOrReplaceConfigMap("secret/configmap.yaml");
        createOrReplaceSecret("secret/secret.yaml");

        ctx = new SpringApplicationBuilder(Empty.class).profiles("secret").run();
    }

    @AfterAll
    static void recover() {
        deleteConfigMap("secret/configmap.yaml");
        deleteSecret("secret/secret.yaml");

        ctx.close();
    }

    @Test
    void testSecret() {
        ConfigurableEnvironment env = ctx.getEnvironment();
        assertThat(env.getProperty("username")).isNotEqualTo("admin");
        assertThat(env.getProperty("password")).isNotEqualTo("cm9vdAo=");
        assertThat(env.getProperty("username")).isEqualTo("root"); // cm9vdAo=
        assertThat(env.getProperty("password")).isNotEqualTo("666");
        assertThat(env.getProperty("password")).isNotEqualTo("MTEyMzIyMwo=");
        assertThat(env.getProperty("password")).isEqualTo("1123223"); // MTEyMzIyMwo=
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();
    }
}
