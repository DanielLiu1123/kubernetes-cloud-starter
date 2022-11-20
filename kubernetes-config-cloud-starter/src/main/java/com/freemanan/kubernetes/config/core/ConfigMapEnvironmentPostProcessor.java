package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Exister.clean;
import static com.freemanan.kubernetes.config.util.Exister.markNotExistFromBeginning;
import static com.freemanan.kubernetes.config.util.KubernetesUtil.kubernetesClient;
import static com.freemanan.kubernetes.config.util.Processors.fileProcessors;
import static com.freemanan.kubernetes.config.util.Util.configMapKey;
import static com.freemanan.kubernetes.config.util.Util.namespace;
import static com.freemanan.kubernetes.config.util.Util.refreshable;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import com.freemanan.kubernetes.config.file.FileProcessor;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
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
    private final List<FileProcessor> fileProcessors;

    public ConfigMapEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
        this.client = kubernetesClient();
        this.fileProcessors = fileProcessors();
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
        List<PropertySource<?>> remotePropertySourceList = new ArrayList<>();
        properties.getConfigMaps().forEach(cm -> addRemotePropertySource(remotePropertySourceList, cm, properties));
        MutablePropertySources propertySources = environment.getPropertySources();
        switch (properties.getPreference()) {
            case LOCAL:
                remotePropertySourceList.forEach(propertySources::addLast);
                break;
            case REMOTE:
                // we can't let it override the system environment properties
                remotePropertySourceList.forEach(ps ->
                        propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, ps));
                break;
            default:
                throw new IllegalArgumentException("Unknown preference: " + properties.getPreference());
        }

        // After the first call, mark it as a refresh event.
        isRefreshEvent.set(true);
    }

    private void addRemotePropertySource(
            List<PropertySource<?>> remotePropertySourceList,
            KubernetesConfigProperties.ConfigMap cm,
            KubernetesConfigProperties properties) {
        if (isRefreshEvent.get() && !refreshable(cm, properties)) {
            // If this is a refresh event, we need to ignore the ConfigMap that not enabled auto refresh.
            return;
        }
        ConfigMap configMap = client.configMaps()
                .inNamespace(namespace(cm, properties))
                .withName(cm.getName())
                .get();
        if (configMap == null) {
            markNotExistFromBeginning(configMapKey(cm, properties));
            log.warn("ConfigMap '" + cm.getName() + "' not found in namespace '" + namespace(cm, properties) + "'");
            return;
        }
        Map<String, String> data = configMap.getData();
        Map<String, Object> pairs = new LinkedHashMap<>(); // store key-value pair
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            boolean isFile = false;

            for (FileProcessor fileProcessor : fileProcessors) {
                if (fileProcessor.hit(key)) {
                    remotePropertySourceList.add(
                            fileProcessor.generate(propertySourceName(key, cm, properties), value));
                    isFile = true;
                    log.info("Loaded configmap: " + cm.getName() + ", key: " + key);
                    break;
                }
            }

            if (!isFile) {
                // key-value pair
                pairs.put(key, value);
            }
        }
        if (!pairs.isEmpty()) {
            remotePropertySourceList.add(new MapPropertySource(propertySourceName("pairs", cm, properties), pairs));
        }
    }

    private static String propertySourceName(
            String key, KubernetesConfigProperties.ConfigMap cm, KubernetesConfigProperties properties) {
        return String.format("%s[configmap:%s,namespace:%s]", key, cm.getName(), namespace(cm, properties));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
