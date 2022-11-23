package com.freemanan.kubernetes.config.core;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * {@link PropertySource} that contains a single key-value pair.
 *
 * @author Freeman
 */
public class SinglePairPropertySource extends MapPropertySource {

    public SinglePairPropertySource(String propertySourceName, String key, Object value) {
        super(propertySourceName, Collections.singletonMap(key, value));
    }

    public Map.Entry<String, Object> getSinglePair() {
        Map<String, Object> source = getSource();
        return source.entrySet().iterator().next();
    }
}
