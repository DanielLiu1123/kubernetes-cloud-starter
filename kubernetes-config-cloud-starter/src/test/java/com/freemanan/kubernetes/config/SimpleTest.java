package com.freemanan.kubernetes.config;

import com.freemanan.kubernetes.config.testsupport.KubernetesTestUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Freeman
 * @since 2022/11/20
 */
public class SimpleTest {

    @Test
    void testConfgi() {
        KubernetesTestUtil.createOrReplaceConfigMap("normal/configmap.yaml");
        //        KubernetesTestUtil.createOrReplaceConfigMap("normal/configmap-changed.yaml");
    }
}
