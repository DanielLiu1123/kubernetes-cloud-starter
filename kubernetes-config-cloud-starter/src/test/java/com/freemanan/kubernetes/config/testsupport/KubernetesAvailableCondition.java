package com.freemanan.kubernetes.config.testsupport;

import com.freemanan.kubernetes.config.util.KubernetesUtil;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Freeman
 */
public class KubernetesAvailableCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        try {
            KubernetesUtil.kubernetesClient();
        } catch (Throwable e) {
            return ConditionEvaluationResult.disabled("Kubernetes unavailable");
        }
        return ConditionEvaluationResult.enabled("Kubernetes available");
    }
}
