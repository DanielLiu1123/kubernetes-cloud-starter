package com.freemanan.kubernetes.config;

import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.createOrReplaceSecret;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteConfigMap;
import static com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil.deleteSecret;
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
@ActiveProfiles("secret")
public class SecretIntegrationTests {

    @BeforeAll
    static void init() {
        createOrReplaceConfigMap("secret/configmap.yaml");
        createOrReplaceSecret("secret/secret.yaml");
    }

    @AfterAll
    static void recover() {
        deleteConfigMap("secret/configmap.yaml");
        deleteSecret("secret/secret.yaml");
    }

    @Autowired
    private Environment env;

    @Test
    void testSecret() {
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
