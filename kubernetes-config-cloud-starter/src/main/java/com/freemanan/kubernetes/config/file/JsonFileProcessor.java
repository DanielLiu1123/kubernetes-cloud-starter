package com.freemanan.kubernetes.config.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Freeman
 */
public class JsonFileProcessor implements FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(JsonFileProcessor.class);

    private static final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    private static final Yaml yaml = new Yaml();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hit(String fileName) {
        return fileName.endsWith(".json");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public EnumerablePropertySource<?> generate(String name, String content) {
        if (content.trim().startsWith("{")) {
            // json object
            return convertJsonObjectStringToPropertySource(name, content);
        }
        CompositePropertySource result = new CompositePropertySource(name);
        try {
            List list = objectMapper.readValue(content, List.class);
            if (list.isEmpty()) {
                return result;
            }
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                if (o instanceof Map) {
                    result.addPropertySource(convertJsonObjectStringToPropertySource(
                            String.format("%s[%d]", name, i), objectMapper.writeValueAsString(o)));
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse json file", e);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    private static CompositePropertySource convertJsonObjectStringToPropertySource(
            String name, String jsonObjectString) {
        // We don't want to change the Spring default behavior
        // this is how we convert json to PropertySource
        // json -> java.util.Map -> yaml -> PropertySource
        Map map = new HashMap<>();
        try {
            map = objectMapper.readValue(jsonObjectString, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse json file", e);
        }
        CompositePropertySource propertySource = new CompositePropertySource(name);
        try {
            String yamlString = yaml.dump(map);
            List<PropertySource<?>> pss =
                    loader.load(name, new ByteArrayResource(yamlString.getBytes(StandardCharsets.UTF_8)));
            propertySource.getPropertySources().addAll(pss);
        } catch (IOException e) {
            log.warn("Failed to parse yaml file", e);
        }
        return propertySource;
    }
}
