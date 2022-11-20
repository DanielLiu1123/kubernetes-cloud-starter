package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Exister.isExistFromBeginning;
import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.refreshable;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
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

/**
 * Watcher for ConfigMap changes.
 *
 * @author Freeman
 */
public class ConfigMapWatcher
        implements ApplicationListener<ApplicationReadyEvent>, ApplicationEventPublisherAware, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ConfigMapWatcher.class);

    private final Map<ConfigMapKey, SharedIndexInformer<ConfigMap>> informers = new LinkedHashMap<>();

    private ApplicationEventPublisher publisher;

    public ConfigMapWatcher(KubernetesConfigProperties properties, KubernetesClient client) {
        initInformersForEachRefreshableConfigMap(properties, client);
    }

    private void initInformersForEachRefreshableConfigMap(
            KubernetesConfigProperties properties, KubernetesClient client) {
        properties.getConfigMaps().stream()
                .filter(cm -> refreshable(cm, properties))
                .forEach(cm -> informers.put(
                        new ConfigMapKey(cm.getName(), namespace(cm, properties), refreshable(cm, properties)),
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
        informers.forEach((cm, informer) -> informer.addEventHandler(new ConfigMapEventHandler(publisher, cm)));
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
    public void destroy() throws Exception {
        informers.values().forEach(SharedIndexInformer::close);
        log.info("ConfigMap informers closed");
    }

    private static class ConfigMapEventHandler implements ResourceEventHandler<ConfigMap> {
        private static final Logger log = LoggerFactory.getLogger(ConfigMapEventHandler.class);

        private final ApplicationEventPublisher publisher;
        private final ConfigMapKey configMap;
        private final AtomicBoolean canRefresh = new AtomicBoolean(false);

        private ConfigMapEventHandler(ApplicationEventPublisher publisher, ConfigMapKey configMap) {
            this.publisher = publisher;
            this.configMap = configMap;
        }

        @Override
        public void onAdd(ConfigMap obj) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "ConfigMap '{}' added in namespace '{}'",
                        obj.getMetadata().getName(),
                        obj.getMetadata().getNamespace());
            }
            // 1. If there is no ConfigMap at the beginning, when add a ConfigMap, we should trigger a refresh event
            // 2. If there is a ConfigMap at the beginning, when application start up, the informer will trigger an
            // onAdd event, but at this phase, we don't want to trigger a refresh event
            if (!isExistFromBeginning(configMap) || canRefresh.getAndSet(true)) {
                refresh();
            }
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
            refresh();
        }

        private void refresh() {
            publisher.publishEvent(new RefreshEvent(this, null, "ConfigMap changed"));
        }
    }
}
