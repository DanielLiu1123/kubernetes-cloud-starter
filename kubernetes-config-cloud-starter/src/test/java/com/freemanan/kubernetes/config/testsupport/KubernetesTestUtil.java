package com.freemanan.kubernetes.config.testsupport;

import com.freemanan.kubernetes.config.util.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Freeman
 */
@SuppressWarnings("unchecked")
public class KubernetesTestUtil {

    static KubernetesClient cli = KubernetesUtil.kubernetesClient();

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

    public static List<StatusDetails> deleteConfigMap(String classpathFile) {
        return cli.resource(configMap(classpathFile)).delete();
    }

    public static Secret createOrReplaceSecret(String classpathFile) {
        return cli.resource(secret(classpathFile)).createOrReplace();
    }

    public static List<StatusDetails> deleteSecret(String classpathFile) {
        return cli.resource(secret(classpathFile)).delete();
    }
}
