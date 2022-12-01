package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Converter.toPropertySource;
import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.preference;
import static com.freemanan.kubernetes.config.util.Util.refreshable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.util.ConfigPreference;
import com.freemanan.kubernetes.config.util.KubernetesClientHolder;
import com.freemanan.kubernetes.config.util.Pair;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
public class ConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    /**
     * If this is not the first call, we consider it as a refresh event.
     * <p> Note: there are multiple applications in the same JVM when running tests, so we need to mark the application.
     */
    private static final Map</*app hashcode*/ Integer, Boolean> isRefreshingMap = new HashMap<>();

    private static final AtomicReference<SpringApplication> currentApplication = new AtomicReference<>();

    private final Log log;
    private final KubernetesClient client;

    public ConfigEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
        this.client = KubernetesClientHolder.getKubernetesClient();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        currentApplication.set(application);
        Boolean enabled = environment.getProperty(KubernetesConfigProperties.PREFIX + ".enabled", Boolean.class, true);
        if (!enabled) {
            return;
        }
        KubernetesConfigProperties properties = Binder.get(environment)
                .bind(KubernetesConfigProperties.PREFIX, KubernetesConfigProperties.class)
                .get();

        // TODO: can we recognize the refresh event and only refresh the changed resources?
        pullConfigMaps(properties, environment);
        pullSecrets(properties, environment);

        // After the first call, mark it as a refresh event.
        isRefreshingMap.put(System.identityHashCode(application), true);
        currentApplication.set(null);
    }

    private void pullConfigMaps(KubernetesConfigProperties properties, ConfigurableEnvironment environment) {
        properties.getConfigMaps().stream()
                .map(configmap -> Optional.ofNullable(propertySourceForConfigMap(configmap, properties))
                        .map(ps -> Pair.of(preference(configmap, properties), ps))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(groupingBy(Pair::key, mapping(Pair::value, toList())))
                .forEach((configPreference, remotePropertySources) -> {
                    addPropertySourcesToEnvironment(environment, configPreference, remotePropertySources);
                });
    }

    private void pullSecrets(KubernetesConfigProperties properties, ConfigurableEnvironment environment) {
        properties.getSecrets().stream()
                .map(secret -> Optional.ofNullable(propertySourceForSecret(secret, properties))
                        .map(ps -> Pair.of(preference(secret, properties), ps))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(groupingBy(Pair::key, mapping(Pair::value, toList())))
                .forEach((configPreference, remotePropertySources) -> {
                    addPropertySourcesToEnvironment(environment, configPreference, remotePropertySources);
                });
    }

    private static <T> void addPropertySourcesToEnvironment(
            ConfigurableEnvironment environment,
            ConfigPreference configPreference,
            List<EnumerablePropertySource<T>> remotePropertySources) {
        MutablePropertySources propertySources = environment.getPropertySources();
        switch (configPreference) {
            case LOCAL -> {
                // The latter config should win the previous config
                Collections.reverse(remotePropertySources);
                remotePropertySources.forEach(propertySources::addLast);
            }
            case REMOTE ->
            // we can't let it override the system environment properties
            remotePropertySources.forEach(
                    ps -> propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, ps));
            default -> throw new IllegalArgumentException("Unknown config preference: " + configPreference.name());
        }
    }

    private EnumerablePropertySource<?> propertySourceForConfigMap(
            KubernetesConfigProperties.ConfigMap cm, KubernetesConfigProperties properties) {
        if (noNeedToReloadResource(refreshable(cm, properties))) {
            return null;
        }
        ConfigMap configMap = client.configMaps()
                .inNamespace(namespace(cm, properties))
                .withName(cm.getName())
                .get();
        if (configMap == null) {
            log.warn(String.format(
                    "ConfigMap '%s' not found in namespace '%s'", cm.getName(), namespace(cm, properties)));
            return null;
        }
        return toPropertySource(configMap);
    }

    private EnumerablePropertySource<?> propertySourceForSecret(
            KubernetesConfigProperties.Secret secret, KubernetesConfigProperties properties) {
        if (noNeedToReloadResource(refreshable(secret, properties))) {
            return null;
        }
        Secret secretInK8s = client.secrets()
                .inNamespace(namespace(secret, properties))
                .withName(secret.getName())
                .get();
        if (secretInK8s == null) {
            log.warn(String.format(
                    "Secret '%s' not found in namespace '%s'", secret.getName(), namespace(secret, properties)));
            return null;
        }
        return toPropertySource(secretInK8s);
    }

    private static boolean noNeedToReloadResource(boolean refreshable) {
        // If this is a refresh event, we need to ignore the resource that not enabled auto refresh.
        return isRefreshingMap.getOrDefault(System.identityHashCode(currentApplication.get()), false) && !refreshable;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
