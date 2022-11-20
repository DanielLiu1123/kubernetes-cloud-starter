package com.freemanan.kubernetes.config.testsupport;

import com.freemanan.kubernetes.config.util.KubernetesUtil;
import io.fabric8.kubernetes.client.Config;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.util.Assert;

/**
 * @author Freeman
 */
public class KubernetesAvailableCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        try {
            Config config = KubernetesUtil.config();
            Assert.notEmpty(config.getContexts(), "No contexts found in kubernetes config");
        } catch (Throwable e) {
            return ConditionEvaluationResult.disabled("Kubernetes unavailable");
        }
        return ConditionEvaluationResult.enabled("Kubernetes available");
    }
}
