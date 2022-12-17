package com.freemanan.kubernetes.grey;

import static com.freemanan.kubernetes.grey.KubernetesGreyProperties.PREFIX;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.predicate.Matcher;
import com.freemanan.kubernetes.grey.predicate.ReactiveMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 */
@ConfigurationProperties(PREFIX)
public class KubernetesGreyProperties implements InitializingBean {
    public static final String PREFIX = "microservice-base.kubernetes.grey";

    private boolean enabled = true;

    private List<Rule> rules = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {
        private String name;
        private Predicates predicates = new Predicates();
        private List<Grey> mappings = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Predicates getPredicates() {
            return predicates;
        }

        public void setPredicates(Predicates predicates) {
            this.predicates = predicates;
        }

        public List<Grey> getMappings() {
            return mappings;
        }

        public void setMappings(List<Grey> mappings) {
            this.mappings = mappings;
        }

        public static class Predicates {
            private List<Header> headers = new ArrayList<>();
            private Class<? extends Matcher> matcherClass;
            private Class<? extends ReactiveMatcher> reactiveMatcherClass;

            public List<Header> getHeaders() {
                return headers;
            }

            public void setHeaders(List<Header> headers) {
                this.headers = headers;
            }

            public Class<? extends Matcher> getMatcherClass() {
                return matcherClass;
            }

            public void setMatcherClass(Class<? extends Matcher> matcherClass) {
                this.matcherClass = matcherClass;
            }

            public Class<? extends ReactiveMatcher> getReactiveMatcherClass() {
                return reactiveMatcherClass;
            }

            public void setReactiveMatcherClass(Class<? extends ReactiveMatcher> reactiveMatcherClass) {
                this.reactiveMatcherClass = reactiveMatcherClass;
            }

            public static class Header {
                private String name;
                private String pattern;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPattern() {
                    return pattern;
                }

                public void setPattern(String pattern) {
                    this.pattern = pattern;
                }
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
