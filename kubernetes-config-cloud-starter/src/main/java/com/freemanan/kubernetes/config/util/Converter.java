package com.freemanan.kubernetes.config.util;

import static com.freemanan.kubernetes.config.util.Processors.fileProcessors;

import com.freemanan.kubernetes.config.core.SinglePairPropertySource;
import com.freemanan.kubernetes.config.file.FileProcessor;
import io.fabric8.kubernetes.api.model.ConfigMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;

/**
 * @author Freeman
 */
public final class Converter {
    private Converter() {
        throw new UnsupportedOperationException("No Converter instances for you!");
    }

    private static EnumerablePropertySource<?> toPropertySource(String key, String content, String propertySourceName) {
        for (FileProcessor fileProcessor : fileProcessors()) {
            if (fileProcessor.hit(key)) {
                return fileProcessor.generate(propertySourceName, content);
            }
        }
        // key-value pair
        return new SinglePairPropertySource(propertySourceName, key, content);
    }

    /**
     * Generate a {@link EnumerablePropertySource} from a {@link ConfigMap}.
     *
     * @param configMap the config map
     * @return the property source
     */
    public static EnumerablePropertySource<?> toPropertySource(ConfigMap configMap) {
        final String propertySourceName = propertySourceNameForConfigMap(configMap);
        CompositePropertySource compositePropertySource = new CompositePropertySource(propertySourceName);
        List<SinglePairPropertySource> singlePairPropertySources = new ArrayList<>();
        configMap.getData().forEach((key, content) -> {
            EnumerablePropertySource<?> ps = toPropertySource(key, content, propertySourceName + "[" + key + "]");
            if (ps instanceof SinglePairPropertySource) {
                singlePairPropertySources.add((SinglePairPropertySource) ps);
            } else {
                compositePropertySource.addPropertySource(ps);
            }
        });
        if (!singlePairPropertySources.isEmpty()) {
            Map<String, Object> pairProperties = singlePairPropertySources.stream()
                    .map(SinglePairPropertySource::getSinglePair)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> newValue,
                            LinkedHashMap::new));
            compositePropertySource.addPropertySource(
                    new MapPropertySource(propertySourceName + "[pair]", pairProperties));
        }
        return compositePropertySource;
    }

    /**
     * Generate property source name for config map.
     *
     * @param configMap the config map
     * @return the property source name
     */
    public static String propertySourceNameForConfigMap(ConfigMap configMap) {
        return String.format(
                "configmap:%s.%s",
                configMap.getMetadata().getName(), configMap.getMetadata().getNamespace());
    }
}
