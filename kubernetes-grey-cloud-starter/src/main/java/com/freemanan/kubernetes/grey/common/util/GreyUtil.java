package com.freemanan.kubernetes.grey.common.util;

import com.freemanan.kubernetes.grey.common.Target;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Freeman
 */
@UtilityClass
public class GreyUtil {
    private static final Logger log = LoggerFactory.getLogger(GreyUtil.class);

    private static final boolean isInK8s = isInKubernetesEnvironment();
    private static final String namespace = getNamespace();
    private static final String suffix = getSuffix();

    /**
     * Check if in Kubernetes environment
     */
    private static boolean isInKubernetesEnvironment() {
        return Files.exists(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token"));
    }

    private static String getNamespace() {
        try {
            String namespace = Files.readString(
                            Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace"),
                            StandardCharsets.UTF_8)
                    .trim();
            if (log.isDebugEnabled()) {
                log.debug("Application is in Kubernetes environment, namespace: '{}'", namespace);
            }
            return namespace;
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Application is not in Kubernetes environment.");
            }
            return "";
        }
    }

    private static String getSuffix() {
        return StringUtils.hasText(namespace) ? "." + namespace : "";
    }

    public static URI grey(URI uri, Map<String, List<Target>> mapping) {
        String authority = uri.getAuthority();
        List<Target> targets = getMatchedRule(authority, mapping);
        if (targets.isEmpty()) {
            return uri;
        }

        // calculate total weight
        double total = targets.stream().mapToDouble(Target::getWeight).sum();
        if (total < 100) {
            targets.add(new Target(authority, 100 - total));
            total = 100;
        }

        // random select
        double randomWeight = Math.random() * total;
        double weight = 0;
        for (Target target : targets) {
            weight += target.getWeight();
            if (randomWeight <= weight) {
                return replaceAuthority(uri, target.getAuthority());
            }
        }
        return uri;
    }

    private static List<Target> getMatchedRule(String authority, Map<String, List<Target>> mapping) {
        return getMatchedRule(authority, mapping, isInK8s, suffix);
    }

    static List<Target> getMatchedRule(
            String authority, Map<String, List<Target>> mapping, boolean isInK8s, String suffix) {
        List<Target> result = mapping.get(authority);
        if (result == null && isInK8s) {
            result = mapping.get(authority + suffix);
            if (result == null) {
                int i = authority.lastIndexOf(suffix);
                if (i > 0) {
                    result = mapping.get(authority.substring(0, i));
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    private static URI replaceAuthority(URI uri, String newAuthority) {
        String oldAuthority = uri.getAuthority();
        if (Objects.equals(oldAuthority, newAuthority)) {
            return uri;
        }
        StringBuilder sb = new StringBuilder(uri.toString());
        int start = sb.indexOf(oldAuthority);
        sb.replace(start, start + oldAuthority.length(), newAuthority);
        return URI.create(sb.toString());
    }
}
