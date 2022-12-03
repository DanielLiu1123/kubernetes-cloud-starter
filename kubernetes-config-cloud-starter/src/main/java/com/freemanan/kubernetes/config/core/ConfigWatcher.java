package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.refreshable;
import static com.freemanan.kubernetes.config.util.Util.resourceKey;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Watcher for config resource changes.
 *
 * @author Freeman
 */
public class ConfigWatcher
        implements SmartInitializingSingleton, ApplicationContextAware, EnvironmentAware, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ConfigWatcher.class);

    private final Map<ResourceKey, Watch> configmapWatchers = new LinkedHashMap<>();
    private final Map<ResourceKey, Watch> secretWatchers = new LinkedHashMap<>();
    private final KubernetesConfigProperties properties;
    private final KubernetesClient client;

    private ApplicationContext context;
    private ConfigurableEnvironment environment;

    public ConfigWatcher(KubernetesConfigProperties properties, KubernetesClient client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            throw new IllegalStateException("Environment must be an instance of ConfigurableEnvironment");
        }
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        watchingRefreshableResources(properties);
    }

    @Override
    public void destroy() {
        configmapWatchers.values().forEach(Watch::close);
        secretWatchers.values().forEach(Watch::close);
        log.info("ConfigMap and Secret watchers closed");
    }

    private void watchingRefreshableResources(KubernetesConfigProperties properties) {
        properties.getConfigMaps().stream()
                .filter(cm -> refreshable(cm, properties))
                .forEach(cm -> configmapWatchers.put(
                        resourceKey(cm, properties), configmapWatcher(namespace(cm, properties), cm.getName())));
        log(configmapWatchers);

        properties.getSecrets().stream()
                .filter(secret -> refreshable(secret, properties))
                .forEach(secret -> secretWatchers.put(
                        resourceKey(secret, properties),
                        secretWatcher(namespace(secret, properties), secret.getName())));
        log(secretWatchers);
    }

    private Watch configmapWatcher(String namespace, String name) {
        return client.configMaps()
                .inNamespace(namespace)
                .withName(name)
                .watch(new HasMetadataWatcher<>(
                        new HasMetadataResourceEventHandler<>(context, environment, properties)));
    }

    private Watch secretWatcher(String namespace, String name) {
        return client.secrets()
                .inNamespace(namespace)
                .withName(name)
                .watch(new HasMetadataWatcher<>(
                        new HasMetadataResourceEventHandler<>(context, environment, properties)));
    }

    private void log(Map<ResourceKey, Watch> watchers) {
        List<String> names = watchers.keySet().stream()
                .map(resourceKey -> String.join(".", resourceKey.getName(), resourceKey.getNamespace()))
                .collect(Collectors.toList());
        if (!names.isEmpty() && log.isInfoEnabled()) {
            log.info(
                    "Start watching {}s: {}",
                    watchers.keySet().iterator().next().getType(),
                    names);
        }
    }

    private static class HasMetadataWatcher<T extends HasMetadata> implements Watcher<T> {

        private final HasMetadataResourceEventHandler<T> handler;

        private HasMetadataWatcher(HasMetadataResourceEventHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        public void eventReceived(Action action, T resource) {
            switch (action) {
                case ADDED:
                    handler.onAdd(resource);
                    break;
                case MODIFIED:
                    handler.onUpdate(null, resource);
                    break;
                case DELETED:
                    handler.onDelete(resource, false);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClose(KubernetesClientException cause) {}
    }
}
