package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteConfigMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import com.freemanan.kubernetes.config.testsupport.KubernetesAvailable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Freeman
 */
@KubernetesAvailable
@SpringBootTest(classes = Empty.class, webEnvironment = NONE)
@ActiveProfiles("not-refreshable")
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

    @Autowired
    private Environment env;

    @Test
    void testNotRefreshable() throws InterruptedException {
        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("666");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        // update configmap-01
        createOrReplaceConfigMap("not_refreshable/configmap-01-changed.yaml");

        // context is refreshing
        Thread.sleep(1000);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(env.getProperty("hobbies[2]")).isNull();

        // update configmap-02
        createOrReplaceConfigMap("not_refreshable/configmap-02-changed.yaml");

        // context is refreshing
        Thread.sleep(1000);

        assertThat(env.getProperty("username")).isEqualTo("admin");
        assertThat(env.getProperty("password")).isEqualTo("888");
        assertThat(env.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(env.getProperty("hobbies[1]")).isNotEqualTo("singing");
        assertThat(env.getProperty("hobbies[2]")).isNull();
    }
}
