package com.freemanan.kubernetes.discovery;

import static com.freemanan.kubernetes.discovery.KubernetesDiscoveryProperties.PREFIX;

import com.freemanan.kubernetes.commons.K8s;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 */
@ConfigurationProperties(PREFIX)
public class KubernetesDiscoveryProperties {
    public static final String PREFIX = "microservice-base.kubernetes.discovery";

    private boolean enabled = true;
    private List<String> namespaces = Collections.singletonList(K8s.currentNamespace());
    private List<String> patterns = Collections.singletonList(".*");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
