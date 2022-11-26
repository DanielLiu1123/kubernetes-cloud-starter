package com.freemanan.kubernetes.config.core;

import com.freemanan.kubernetes.config.util.Pair;
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

    public Pair<String, Object> getSinglePair() {
        Map<String, Object> source = getSource();
        Map.Entry<String, Object> entry = source.entrySet().iterator().next();
        return Pair.of(entry.getKey(), entry.getValue());
    }
}
