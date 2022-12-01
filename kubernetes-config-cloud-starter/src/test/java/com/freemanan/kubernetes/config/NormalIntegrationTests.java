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
@ActiveProfiles("normal")
public class NormalIntegrationTests {

    @BeforeAll
    static void init() {
        createOrReplaceConfigMap("normal/configmap.yaml");
    }

    @AfterAll
    static void recover() {
        deleteConfigMap("normal/configmap-changed.yaml");
    }

    @Autowired
    private Environment env;

    @Test
    void testNormal() throws InterruptedException {
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
