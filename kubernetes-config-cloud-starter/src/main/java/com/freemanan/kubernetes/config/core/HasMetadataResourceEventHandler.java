package com.freemanan.kubernetes.config.core;

import static com.freemanan.kubernetes.config.util.Converter.propertySourceNameForResource;

import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
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
    private final ConfigurableEnvironment environment;
    private final KubernetesConfigProperties properties;

    public HasMetadataResourceEventHandler(
            ApplicationEventPublisher publisher,
            ConfigurableEnvironment environment,
            KubernetesConfigProperties properties) {
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
        // When application start up, the informer will trigger an onAdd event, but at this phase application is not
        // ready, and it will not trigger a real refresh.
        // see org.springframework.cloud.endpoint.event.RefreshEventListener#handle(RefreshEvent)
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
