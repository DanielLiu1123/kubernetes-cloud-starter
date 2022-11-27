package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteConfigMap;
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
public class NormalIntegrationTests {

    static ConfigurableApplicationContext ctx;

    @BeforeAll
    static void init() {
        createOrReplaceConfigMap("normal/configmap.yaml");

        ctx = new SpringApplicationBuilder(Empty.class).profiles("normal").run();
    }

    @AfterAll
    static void recover() {
        deleteConfigMap("normal/configmap-changed.yaml");

        ctx.close();
    }

    @Test
    void testNormal() throws InterruptedException {
        ConfigurableEnvironment env = ctx.getEnvironment();
        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("666");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        // update configmap
        createOrReplaceConfigMap("normal/configmap-changed.yaml");

        // context is refreshing
        Thread.sleep(1500);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isEqualTo("coding");

        // delete configmap, refresh on delete is disabled by default
        deleteConfigMap("normal/configmap-changed.yaml");

        // context is refreshing
        Thread.sleep(1500);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isEqualTo("coding");
    }
}
