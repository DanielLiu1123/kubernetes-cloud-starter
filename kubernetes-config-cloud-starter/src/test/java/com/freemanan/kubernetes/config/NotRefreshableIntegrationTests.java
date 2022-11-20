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
public class NotRefreshableIntegrationTests {

    @BeforeAll
    static void init() {
        createOrReplaceConfigMap("not_refreshable/configmap-01.yaml");
        createOrReplaceConfigMap("not_refreshable/configmap-02.yaml");
    }

    @AfterAll
    static void recover() {
        deleteConfigMap("not_refreshable/configmap-01-changed.yaml");
        deleteConfigMap("not_refreshable/configmap-02-changed.yaml");
    }

    @Test
    void testNotRefreshable() throws InterruptedException {
        // start app
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Empty.class)
                .profiles("not-refreshable")
                .run();

        ConfigurableEnvironment env = ctx.getEnvironment();
        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("666");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        // update configmap-01
        createOrReplaceConfigMap("not_refreshable/configmap-01-changed.yaml");

        // context is refreshing
        Thread.sleep(1500);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        // update configmap-02
        createOrReplaceConfigMap("not_refreshable/configmap-02-changed.yaml");

        // context is refreshing
        Thread.sleep(1500);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isNotEqualTo("singing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        ctx.close();
    }
}
