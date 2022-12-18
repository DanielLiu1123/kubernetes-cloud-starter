package com.freemanan.kubernetes.config.testsupport;

import com.freemanan.kubernetes.commons.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Freeman
 */
public class KubernetesTestUtil {

    static KubernetesClient cli = KubernetesUtil.newKubernetesClient();

    public static ConfigMap configMap(String classpathFile) {
        try {
            return cli.configMaps()
                    .load(new ClassPathResource(classpathFile).getURL())
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Secret secret(String classpathFile) {
        try {
            return cli.secrets()
                    .load(new ClassPathResource(classpathFile).getURL())
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigMap createOrReplaceConfigMap(String classpathFile) {
        return cli.resource(configMap(classpathFile)).createOrReplace();
    }

    public static void deleteConfigMap(String classpathFile) {
        cli.resource(configMap(classpathFile)).delete();
    }

    public static Secret createOrReplaceSecret(String classpathFile) {
        return cli.resource(secret(classpathFile)).createOrReplace();
    }

    public static void deleteSecret(String classpathFile) {
        cli.resource(secret(classpathFile)).delete();
    }
}
