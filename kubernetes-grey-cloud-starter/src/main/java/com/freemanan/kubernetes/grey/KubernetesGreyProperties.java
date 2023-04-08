package com.freemanan.kubernetes.grey;

import static com.freemanan.kubernetes.grey.KubernetesGreyProperties.PREFIX;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.predicate.Matcher;
import com.freemanan.kubernetes.grey.predicate.ReactiveMatcher;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {"condition": "#header['user-id'] > 1000", "order": 0, "rules": {"svc.ns:8080": [{"target":"svc-v-v1.ns:8080", "weight":30}, {"target":"svc-v-v2.ns:8080", "weight":50}]}}
 * {"condition": "#header['grey-name'] = 'v'", "order": 1, "rules": {"svc.ns:8080": [{"target":"svc-v-v1.ns:8080", "weight":30}, {"target":"svc-v-v2.ns:8080", "weight":50}]}}
 *
 * @author Freeman
 */
@Data
@ConfigurationProperties(PREFIX)
public class KubernetesGreyProperties implements InitializingBean {
    public static final String PREFIX = "microservice-base.kubernetes.grey";

    private boolean enabled = true;

    private List<Rule> rules = new ArrayList<>();

    @Data
    public static class Rule {
        private String name;
        private Predicates predicates = new Predicates();
        private List<Grey> mappings = new ArrayList<>();

        @Data
        public static class Predicates {
            private List<Header> headers = new ArrayList<>();
            private Class<? extends Matcher> matcherClass;
            private Class<? extends ReactiveMatcher> reactiveMatcherClass;

            @Data
            public static class Header {
                private String name;
                private String pattern;
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        validate();
    }

    private void validate() {
        for (Rule rule : getRules()) {
            for (Grey grey : rule.getMappings()) {
                grey.validate();
            }
        }
    }
}
