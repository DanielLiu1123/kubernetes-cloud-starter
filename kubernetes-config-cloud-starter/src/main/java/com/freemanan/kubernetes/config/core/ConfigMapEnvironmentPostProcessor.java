package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Exister.clean;
import static com.freemanan.kubernetes.config.util.Exister.markNotExistWhenAppStartup;
import static com.freemanan.kubernetes.config.util.KubernetesUtil.kubernetesClient;
import static com.freemanan.kubernetes.config.util.Util.configMapKey;
import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.preference;
import static com.freemanan.kubernetes.config.util.Util.refreshable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.util.Converter;
import com.freemanan.kubernetes.config.util.Pair;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author Freeman
 */
public class ConfigMapEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    /**
     * If this is not the first call, we consider it as a refresh event.
     */
    private static final AtomicBoolean isRefreshEvent = new AtomicBoolean(false);

    private final Log log;
    private final KubernetesClient client;

    public ConfigMapEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
        this.client = kubernetesClient();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // clean all ConfigMap that marked as not exist
        clean();

        Boolean enabled = environment.getProperty(KubernetesConfigProperties.PREFIX + ".enabled", Boolean.class, true);
        if (!enabled) {
            return;
        }
        KubernetesConfigProperties properties = Binder.get(environment)
                .bind(KubernetesConfigProperties.PREFIX, KubernetesConfigProperties.class)
                .get();
        MutablePropertySources propertySources = environment.getPropertySources();

        properties.getConfigMaps().stream()
                .map(cm -> Optional.ofNullable(propertySourceForConfigMap(cm, properties))
                        .map(ps -> Pair.of(preference(cm, properties), ps))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())))
                .forEach((configPreference, remotePropertySources) -> {
                    switch (configPreference) {
                        case LOCAL:
                            // The latter config should win the previous config
                            Collections.reverse(remotePropertySources);
                            remotePropertySources.forEach(propertySources::addLast);
                            break;
                        case REMOTE:
                            // we can't let it override the system environment properties
                            remotePropertySources.forEach(ps -> propertySources.addAfter(
                                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, ps));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown preference: " + properties.getPreference());
                    }
                });

        // After the first call, mark it as a refresh event.
        isRefreshEvent.set(true);
    }

    private EnumerablePropertySource<?> propertySourceForConfigMap(
            KubernetesConfigProperties.ConfigMap cm, KubernetesConfigProperties properties) {
        if (isRefreshEvent.get() && !refreshable(cm, properties)) {
            // If this is a refresh event, we need to ignore the ConfigMap that not enabled auto refresh.
            return null;
        }
        ConfigMap configMap = client.configMaps()
                .inNamespace(namespace(cm, properties))
                .withName(cm.getName())
                .get();
        if (configMap == null) {
            markNotExistWhenAppStartup(configMapKey(cm, properties));
            log.warn(String.format(
                    "ConfigMap '%s' not found in namespace '%s'", cm.getName(), namespace(cm, properties)));
            return null;
        }
        return Converter.toPropertySource(configMap);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
