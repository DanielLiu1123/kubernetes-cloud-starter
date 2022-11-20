package com.freemanan.kubernetes.config.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;

/**
 * @author Freeman
 */
public class PropertiesFileProcessor implements FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(PropertiesFileProcessor.class);

    private static final PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();

    @Override
    public boolean hit(String fileName) {
        return Arrays.stream(loader.getFileExtensions()).anyMatch(fileName::endsWith);
    }

    @Override
    public PropertySource<?> generate(String name, String content) {
        CompositePropertySource propertySource = new CompositePropertySource(name);
        try {
            List<PropertySource<?>> pss =
                    loader.load(name, new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)));
            propertySource.getPropertySources().addAll(pss);
        } catch (IOException e) {
            log.warn("Failed to parse properties file", e);
        }
        return propertySource;
    }
}
