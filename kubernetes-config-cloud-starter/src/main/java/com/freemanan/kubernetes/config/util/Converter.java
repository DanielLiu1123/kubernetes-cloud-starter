package com.freemanan.kubernetes.config.util;

import static com.freemanan.kubernetes.config.util.Processors.fileProcessors;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.freemanan.kubernetes.config.core.SinglePairPropertySource;
import com.freemanan.kubernetes.config.file.FileProcessor;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * @author Freeman
 */
public final class Converter {
    private Converter() {
        throw new UnsupportedOperationException("No Converter instances for you!");
    }

    private static EnumerablePropertySource<?> toPropertySource(String propertySourceName, Map<String, String> data) {
        CompositePropertySource compositePropertySource = new CompositePropertySource(propertySourceName);
        List<SinglePairPropertySource> singlePairPropertySources = new ArrayList<>();
        data.forEach((key, content) -> {
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
                            Pair::getKey, Pair::getValue, (oldValue, newValue) -> newValue, LinkedHashMap::new));
            compositePropertySource.addPropertySource(
                    new MapPropertySource(propertySourceName + "[pair]", pairProperties));
        }
        return compositePropertySource;
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
        return toPropertySource(propertySourceNameForResource(configMap), configMap.getData());
    }

    /**
     * Generate a {@link EnumerablePropertySource} from a {@link Secret}.
     *
     * @param secret the secret
     * @return the property source
     */
    public static EnumerablePropertySource<?> toPropertySource(Secret secret) {
        // data is base64 encoded
        Map<String, String> data = secret.getData();
        Map<String, Object> encodedValue = new LinkedHashMap<>(data);
        data.replaceAll((key, value) -> StringUtils.trimTrailingWhitespace(
                new String(Base64.getDecoder().decode(value), UTF_8))); // it will add newlines automatically
        Map<String, String> decodedValue = new LinkedHashMap<>(data);
        CompositePropertySource result = new CompositePropertySource(propertySourceNameForResource(secret));
        result.addPropertySource(toPropertySource(propertySourceNameForResource(secret) + "[decoded]", decodedValue));
        result.addPropertySource(
                new MapPropertySource(propertySourceNameForResource(secret) + "[encoded]", encodedValue));
        return result;
    }

    /**
     * Generate property source name for resource that have metadata.
     *
     * @param hasMetadataResource the resource that have metadata
     * @return the property source name
     */
    public static String propertySourceNameForResource(HasMetadata hasMetadataResource) {
        return String.format(
                "%s:%s.%s",
                hasMetadataResource.getKind(),
                hasMetadataResource.getMetadata().getName(),
                hasMetadataResource.getMetadata().getNamespace());
    }
}
