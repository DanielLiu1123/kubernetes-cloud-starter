package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.refreshable;
import static com.freemanan.kubernetes.config.util.Util.resourceKey;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Watcher for ConfigMap changes.
 *
 * @author Freeman
 */
public class ConfigWatcher
        implements SmartInitializingSingleton, ApplicationEventPublisherAware, EnvironmentAware, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ConfigWatcher.class);

    private final Map<ResourceKey, SharedIndexInformer<ConfigMap>> configmapInformers = new LinkedHashMap<>();
    private final Map<ResourceKey, SharedIndexInformer<Secret>> secretInformers = new LinkedHashMap<>();
    private final KubernetesConfigProperties properties;

    private ApplicationEventPublisher publisher;
    private ConfigurableEnvironment environment;

    public ConfigWatcher(KubernetesConfigProperties properties, KubernetesClient client) {
        this.properties = properties;
        initInformersForEachRefreshableResources(properties, client);
    }

    private void initInformersForEachRefreshableResources(
            KubernetesConfigProperties properties, KubernetesClient client) {
        properties.getConfigMaps().stream()
                .filter(cm -> refreshable(cm, properties))
                .forEach(cm -> configmapInformers.put(
                        resourceKey(cm, properties),
                        client.configMaps()
                                .inNamespace(namespace(cm, properties))
                                .withName(cm.getName())
                                .inform()));
        properties.getSecrets().stream()
                .filter(secret -> refreshable(secret, properties))
                .forEach(secret -> secretInformers.put(
                        resourceKey(secret, properties),
                        client.secrets()
                                .inNamespace(namespace(secret, properties))
                                .withName(secret.getName())
                                .inform()));
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            throw new IllegalStateException("Environment must be an instance of ConfigurableEnvironment");
        }
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void afterSingletonsInstantiated() {
        watch(configmapInformers);
        watch(secretInformers);
    }

    @Override
    public void destroy() {
        configmapInformers.values().forEach(SharedIndexInformer::close);
        secretInformers.values().forEach(SharedIndexInformer::close);
        log.info("ConfigMap and Secret informers closed");
    }

    private <T extends HasMetadata> void watch(Map<ResourceKey, SharedIndexInformer<T>> informers) {
        informers.forEach((resourceKey, informer) ->
                informer.addEventHandler(new HasMetadataResourceEventHandler(publisher, environment, properties)));
        List<String> names = informers.keySet().stream()
                .map(resourceKey -> String.join(".", resourceKey.getName(), resourceKey.getNamespace()))
                .collect(Collectors.toList());
        if (!names.isEmpty() && log.isInfoEnabled()) {
            log.info(
                    "Start watching {}s: {}",
                    informers.keySet().iterator().next().getType(),
                    names);
        }
    }
}
