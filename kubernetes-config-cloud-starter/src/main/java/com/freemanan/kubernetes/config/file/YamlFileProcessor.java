package com.freemanan.kubernetes.config.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * @author Freeman
 */
public class YamlFileProcessor implements FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(YamlFileProcessor.class);

    private static final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public boolean hit(String fileName) {
        return Arrays.stream(loader.getFileExtensions()).anyMatch(fileName::endsWith);
    }

    @Override
    public PropertySource<?> generate(String name, String content) {
        Resource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        CompositePropertySource propertySource = new CompositePropertySource(name);
        try {
            List<PropertySource<?>> sources = loader.load(name, resource);
            propertySource.getPropertySources().addAll(sources);
        } catch (IOException e) {
            log.warn("Failed to parse yaml file", e);
        }
        return propertySource;
    }
}
