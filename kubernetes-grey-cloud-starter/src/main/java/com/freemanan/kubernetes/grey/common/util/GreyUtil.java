package com.freemanan.kubernetes.grey.common.util;

import com.freemanan.kubernetes.grey.common.Target;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;

/**
 * @author Freeman
 */
@UtilityClass
public class GreyUtil {

    public static URI grey(URI uri, Map<String, List<Target>> mapping) {
        String authority = uri.getAuthority();
        List<Target> targets = mapping.getOrDefault(authority, Collections.emptyList());
        if (targets.isEmpty()) {
            return uri;
        }

        // 计算总权重
        double total = targets.stream().mapToDouble(Target::getWeight).sum();
        if (total < 100) {
            targets.add(new Target(authority, 100 - total));
            total = 100;
        }

        // 随机选择一个 Target
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

    private static URI replaceAuthority(URI uri, String newAuthority) {
        String oldAuthority = uri.getAuthority();
        if (Objects.equals(oldAuthority, newAuthority)) {
            return uri;
        }
        String newUri = uri.toString().replaceFirst(oldAuthority, newAuthority);
        return URI.create(newUri);
    }
}
