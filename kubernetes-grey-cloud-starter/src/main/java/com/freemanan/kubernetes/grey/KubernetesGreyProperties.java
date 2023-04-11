package com.freemanan.kubernetes.grey;

import static com.freemanan.kubernetes.grey.KubernetesGreyProperties.PREFIX;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {"condition": "#header['user-id'] > 1000", "order": 0, "rules": {"svc.ns:8080": [{"target":"svc-v-v1.ns:8080", "weight":30}, {"target":"svc-v-v2.ns:8080", "weight":50}]}}
 * {"condition": "#header['grey-name'] = 'v'", "order": 1, "rules": {"svc.ns:8080": [{"target":"svc-v-v1.ns:8080", "weight":30}, {"target":"svc-v-v2.ns:8080", "weight":50}]}}
 *
 * @author Freeman
 */
@Data
@ConfigurationProperties(PREFIX)
public class KubernetesGreyProperties {
    public static final String PREFIX = "microservice-base.kubernetes.grey";

    private boolean enabled = true;

    private GreyGateway greyGateway = new GreyGateway();

    @Data
    public static class GreyGateway {
        /**
         * Grey gateway url, default is <span color="orange">grey-gateway:8080</span>
         */
        private String authority = "grey-gateway:8080";

        private String path = "/v1/grey-gateway/greys";
    }
}
