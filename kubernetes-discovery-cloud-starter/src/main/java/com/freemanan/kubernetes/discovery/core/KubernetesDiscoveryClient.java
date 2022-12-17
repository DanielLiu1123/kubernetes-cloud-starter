package com.freemanan.kubernetes.discovery.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemanan.kubernetes.discovery.KubernetesDiscoveryProperties;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * @author Freeman
 */
public class KubernetesDiscoveryClient implements DiscoveryClient {
    private static final Logger log = LoggerFactory.getLogger(KubernetesDiscoveryClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final KubernetesClient kubernetesClient;
    private final KubernetesDiscoveryProperties properties;

    public KubernetesDiscoveryClient(KubernetesClient kubernetesClient, KubernetesDiscoveryProperties properties) {
        this.kubernetesClient = kubernetesClient;
        this.properties = properties;
    }

    @Override
    public String description() {
        return "Kubernetes Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        if (properties.getPatterns().stream().noneMatch(pattern -> Pattern.matches(pattern, serviceId))) {
            log.warn(
                    "Service '{}' does not match any of the patterns {}, return empty list",
                    serviceId,
                    properties.getPatterns());
            return Collections.emptyList();
        }

        List<String> nss = isAllNamespaces()
                ? kubernetesClient.namespaces().list().getItems().stream()
                        .map(ns -> ns.getMetadata().getName())
                        .collect(Collectors.toList())
                : properties.getNamespaces();
        Service svc = null;
        String hitNamespace = null;
        for (String namespace : nss) {
            svc = kubernetesClient
                    .services()
                    .inNamespace(namespace)
                    .withName(serviceId)
                    .get();
            if (svc != null) {
                hitNamespace = namespace;
                break;
            }
        }
        if (svc == null) {
            log.warn("Service '{}' not found in any of the namespaces {}, return empty list", serviceId, nss);
            return Collections.emptyList();
        }

        Map<String, String> selector = svc.getSpec().getSelector();
        List<Pod> pods = kubernetesClient
                .pods()
                .inNamespace(hitNamespace)
                .withLabels(selector)
                .list()
                .getItems();
        if (pods.size() > 0) {
            return pods.stream().map(pod -> genServiceInstance(serviceId, pod)).collect(Collectors.toList());
        }
        log.warn(
                "Service '{}' with labels '{}' not found in namespace '{}', return empty list",
                serviceId,
                selector,
                hitNamespace);
        return Collections.emptyList();
    }

    private static DefaultKubernetesServiceInstance genServiceInstance(String serviceId, Pod pod) {
        return new DefaultKubernetesServiceInstance(
                pod.getMetadata().getUid(),
                serviceId,
                pod.getStatus().getPodIP(),
                pod.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort(),
                genMetadata(pod),
                false,
                pod.getMetadata().getNamespace());
    }

    private static Map<String, String> genMetadata(Pod pod) {
        Map<String, String> result = new LinkedHashMap<>();
        ObjectMeta metadata = pod.getMetadata();
        Optional.ofNullable(metadata.getName()).ifPresent(name -> result.put("name", name));
        Optional.ofNullable(metadata.getNamespace()).ifPresent(namespace -> result.put("namespace", namespace));
        Optional.ofNullable(metadata.getCreationTimestamp())
                .ifPresent(creationTimestamp -> result.put("creationTimestamp", creationTimestamp));
        Optional.ofNullable(metadata.getDeletionTimestamp())
                .ifPresent(deletionTimestamp -> result.put("deletionTimestamp", deletionTimestamp));
        Optional.ofNullable(metadata.getDeletionGracePeriodSeconds())
                .ifPresent(deletionGracePeriodSeconds ->
                        result.put("deletionGracePeriodSeconds", deletionGracePeriodSeconds.toString()));
        Optional.ofNullable(metadata.getFinalizers())
                .ifPresent(finalizers -> result.put("finalizers", finalizers.toString()));
        Optional.ofNullable(metadata.getGenerateName())
                .ifPresent(generateName -> result.put("generateName", generateName));
        Optional.ofNullable(metadata.getGeneration())
                .ifPresent(generation -> result.put("generation", generation.toString()));
        Optional.ofNullable(metadata.getLabels()).ifPresent(labels -> result.put("labels", toJson(labels)));
        Optional.ofNullable(metadata.getOwnerReferences())
                .ifPresent(ownerReferences -> result.put("ownerReferences", toJson(ownerReferences)));
        Optional.ofNullable(metadata.getResourceVersion())
                .ifPresent(resourceVersion -> result.put("resourceVersion", resourceVersion));
        Optional.ofNullable(metadata.getSelfLink()).ifPresent(selfLink -> result.put("selfLink", selfLink));
        Optional.ofNullable(metadata.getUid()).ifPresent(uid -> result.put("uid", uid));
        Optional.ofNullable(metadata.getAnnotations())
                .ifPresent(annotations -> result.put("annotations", toJson(annotations)));
        Optional.ofNullable(metadata.getAdditionalProperties())
                .ifPresent(initializers -> result.put("additionalProperties", toJson(initializers)));
        Optional.ofNullable(metadata.getManagedFields())
                .ifPresent(managedFields -> result.put("managedFields", toJson(managedFields)));
        return result;
    }

    @Override
    public List<String> getServices() {
        Stream<Service> nss = isAllNamespaces()
                ? kubernetesClient.services().inAnyNamespace().list().getItems().stream()
                : properties.getNamespaces().stream()
                        .flatMap(namespace ->
                                kubernetesClient.services().inNamespace(namespace).list().getItems().stream());
        return nss.map(svc -> svc.getMetadata().getName())
                .filter(svc -> properties.getPatterns().stream().anyMatch(pattern -> Pattern.matches(pattern, svc)))
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isAllNamespaces() {
        return properties.getNamespaces().stream().anyMatch(ns -> Objects.equals(ns.trim(), "*"));
    }

    private static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
