package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Converter.propertySourceNameForResource;
import static com.freemanan.kubernetes.config.util.Exister.existWhenPrepareEnvironment;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Freeman
 */
public class HasMetadataResourceEventHandler implements ResourceEventHandler<HasMetadata> {
    private static final Logger log = LoggerFactory.getLogger(HasMetadataResourceEventHandler.class);

    private final ApplicationEventPublisher publisher;
    private final ResourceKey resourceKey;
    private final ConfigurableEnvironment environment;
    private final KubernetesConfigProperties properties;
    private final AtomicBoolean isFirstTrigger = new AtomicBoolean(true);

    public HasMetadataResourceEventHandler(
            ResourceKey resourceKey,
            ApplicationEventPublisher publisher,
            ConfigurableEnvironment environment,
            KubernetesConfigProperties properties) {
        this.resourceKey = resourceKey;
        this.publisher = publisher;
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public void onAdd(HasMetadata obj) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "{} '{}' added in namespace '{}'",
                    obj.getKind(),
                    obj.getMetadata().getName(),
                    obj.getMetadata().getNamespace());
        }
        // 1. If there is no resource from beginning, when add a resource, we should trigger a refresh event.
        // 2. If there is a resource from beginning, when application start up, the informer will trigger an
        // onAdd event, but at this phase, we don't want to trigger a refresh event.
        // So if the resource exist from beginning, and it's the first time to trigger the event, we should just
        // ignore it
        if (existWhenPrepareEnvironment(resourceKey) && isFirstTrigger.getAndSet(false)) {
            return;
        }
        refresh(obj);
    }

    @Override
    public void onUpdate(HasMetadata oldObj, HasMetadata newObj) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "{} '{}' updated in namespace '{}'",
                    newObj.getKind(),
                    newObj.getMetadata().getName(),
                    newObj.getMetadata().getNamespace());
        }
        refresh(newObj);
    }

    @Override
    public void onDelete(HasMetadata obj, boolean deletedFinalStateUnknown) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "{} '{}' deleted in namespace '{}'",
                    obj.getKind(),
                    obj.getMetadata().getName(),
                    obj.getMetadata().getNamespace());
        }
        if (properties.isRefreshOnDelete()) {
            deletePropertySourceOfResource(obj);
            refresh(obj);
        } else {
            log.info("Refresh on delete is disabled, ignore the delete event");
        }
    }

    private void deletePropertySourceOfResource(HasMetadata resource) {
        String propertySourceName = propertySourceNameForResource(resource);
        environment.getPropertySources().remove(propertySourceName);
    }

    private void refresh(HasMetadata obj) {
        publisher.publishEvent(new RefreshEvent(obj, null, String.format("%s changed", obj.getKind())));
    }
}
