package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Exister.existWhenAppStartup;
import static com.freemanan.kubernetes.config.util.Util.configMapKey;
import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.refreshable;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.util.Converter;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Watcher for ConfigMap changes.
 *
 * @author Freeman
 */
public class ConfigMapWatcher
        implements ApplicationListener<ApplicationReadyEvent>,
                ApplicationEventPublisherAware,
                EnvironmentAware,
                DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ConfigMapWatcher.class);

    private final Map<ConfigMapKey, SharedIndexInformer<ConfigMap>> informers = new LinkedHashMap<>();

    private ApplicationEventPublisher publisher;
    private ConfigurableEnvironment environment;

    public ConfigMapWatcher(KubernetesConfigProperties properties, KubernetesClient client) {
        initInformersForEachRefreshableConfigMap(properties, client);
    }

    private void initInformersForEachRefreshableConfigMap(
            KubernetesConfigProperties properties, KubernetesClient client) {
        properties.getConfigMaps().stream()
                .filter(cm -> refreshable(cm, properties))
                .forEach(cm -> informers.put(
                        configMapKey(cm, properties),
                        client.configMaps()
                                .inNamespace(namespace(cm, properties))
                                .withName(cm.getName())
                                .inform()));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        startWatchingConfigMaps();
    }

    private void startWatchingConfigMaps() {
        informers.forEach(
                (cm, informer) -> informer.addEventHandler(new ConfigMapEventHandler(cm, publisher, environment)));
        List<String> configMapNames = informers.keySet().stream()
                .map(cm -> String.join(".", cm.getName(), cm.getNamespace()))
                .collect(Collectors.toList());
        log.info("Start watching ConfigMaps: {}", configMapNames);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void destroy() {
        informers.values().forEach(SharedIndexInformer::close);
        log.info("ConfigMap informers closed");
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            throw new IllegalStateException("Environment must be an instance of ConfigurableEnvironment");
        }
        this.environment = (ConfigurableEnvironment) environment;
    }

    private static class ConfigMapEventHandler implements ResourceEventHandler<ConfigMap> {
        private static final Logger log = LoggerFactory.getLogger(ConfigMapEventHandler.class);

        private final ApplicationEventPublisher publisher;
        private final ConfigMapKey configMap;
        private final AtomicBoolean isFirstTrigger = new AtomicBoolean(true);
        private final ConfigurableEnvironment environment;

        private ConfigMapEventHandler(
                ConfigMapKey configMap, ApplicationEventPublisher publisher, ConfigurableEnvironment environment) {
            this.configMap = configMap;
            this.publisher = publisher;
            this.environment = environment;
        }

        @Override
        public void onAdd(ConfigMap obj) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "ConfigMap '{}' added in namespace '{}'",
                        obj.getMetadata().getName(),
                        obj.getMetadata().getNamespace());
            }
            // 1. If there is no ConfigMap from beginning, when add a ConfigMap, we should trigger a refresh event.
            // 2. If there is a ConfigMap from beginning, when application start up, the informer will trigger an
            // onAdd event, but at this phase, we don't want to trigger a refresh event.
            // So if the ConfigMap exist from beginning, and it's the first time to trigger the event, we should just
            // ignore it
            if (existWhenAppStartup(configMap) && isFirstTrigger.getAndSet(false)) {
                return;
            }
            refresh();
        }

        @Override
        public void onUpdate(ConfigMap oldObj, ConfigMap newObj) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "ConfigMap '{}' updated in namespace '{}'",
                        newObj.getMetadata().getName(),
                        newObj.getMetadata().getNamespace());
            }
            refresh();
        }

        @Override
        public void onDelete(ConfigMap obj, boolean deletedFinalStateUnknown) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "ConfigMap '{}' deleted in namespace '{}'",
                        obj.getMetadata().getName(),
                        obj.getMetadata().getNamespace());
            }
            deletePropertySourceOfConfigMap(obj);
            refresh();
        }

        private void deletePropertySourceOfConfigMap(ConfigMap configMap) {
            String propertySourceName = Converter.propertySourceNameForConfigMap(configMap);
            environment.getPropertySources().remove(propertySourceName);
        }

        private void refresh() {
            publisher.publishEvent(new RefreshEvent(this, null, "ConfigMap changed"));
        }
    }
}
