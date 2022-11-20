package com.freemanan.kubernetes.config.testsupport;

import com.freemanan.kubernetes.config.util.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Freeman
 */
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

    public static ConfigMap createOrReplaceConfigMap(String classpathFile) {
        return cli.configMaps().createOrReplace(configMap(classpathFile));
    }

    public static boolean deleteConfigMap(String classpathFile) {
        return cli.configMaps().delete(configMap(classpathFile));
    }
}
