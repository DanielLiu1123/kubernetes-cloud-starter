package com.freemanan.kubernetes.config.core;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * @author Freeman
 */
public class ConfigMissingException extends RuntimeException {

    static class ConfigMissingFailureAnalyzer extends AbstractFailureAnalyzer<ConfigMissingException> {
        @Override
        protected FailureAnalysis analyze(Throwable rootFailure, ConfigMissingException cause) {
            String description = "Config is missing";
            String action = String.format(
                    "You can set '%s.fail-on-missing-config' to 'false' to not prevent application start up.",
                    KubernetesConfigProperties.PREFIX);
            return new FailureAnalysis(description, action, cause);
        }
    }
}
