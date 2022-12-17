package com.freemanan.kubernetes.grey.server.scg;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import com.freemanan.kubernetes.grey.KubernetesGreyProperties;
import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import com.freemanan.kubernetes.grey.predicate.ReactiveMatcher;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 * @since 2022/12/17
 */
public class GreyGlobalFilter implements GlobalFilter, Ordered {

    /**
     * Before {@link NettyRoutingFilter}, we can modify the request url before routing.
     */
    public static final int ORDER = NettyRoutingFilter.ORDER - 1;

    private final KubernetesGreyProperties properties;

    public GreyGlobalFilter(KubernetesGreyProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<KubernetesGreyProperties.Rule> rules = properties.getRules();

        // only hit one rule
        for (KubernetesGreyProperties.Rule rule : rules) {
            if (match(exchange, rule)) {
                exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders ->
                                httpHeaders.add(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(rule.getMappings())));

                URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
                Grey grey = GreyUtil.getMathchedGrey(requestUrl, rule.getMappings());
                if (grey == null) {
                    // not match any destination, use master
                    return chain.filter(exchange);
                }
                URI greyUri = GreyUtil.grey(requestUrl, grey);
                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, greyUri);
                break;
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    private static boolean match(ServerWebExchange exchange, KubernetesGreyProperties.Rule rule) {
        KubernetesGreyProperties.Rule.Predicates predicates = rule.getPredicates();

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // validate header
        AtomicBoolean hitAllHeaderConditions = new AtomicBoolean(true);
        for (KubernetesGreyProperties.Rule.Predicates.Header header : predicates.getHeaders()) {
            if (!hit(headers, header)) {
                hitAllHeaderConditions.set(false);
                break;
            }
        }
        // quick pass
        if (!hitAllHeaderConditions.get()) {
            return false;
        }

        // validate ReactiveMatcher class
        Class<? extends ReactiveMatcher> matcherClass = predicates.getReactiveMatcherClass();
        if (matcherClass == null) {
            return true;
        }
        ReactiveMatcher matcher = BeanUtils.instantiateClass(matcherClass);
        return matcher.match(exchange);
    }

    private static boolean hit(HttpHeaders headers, KubernetesGreyProperties.Rule.Predicates.Header header) {
        List<String> headerValues = headers.get(header.getName());
        if (CollectionUtils.isEmpty(headerValues)) {
            return false;
        }
        for (String headerValue : headerValues) {
            if (Pattern.matches(header.getPattern(), headerValue)) {
                return true;
            }
        }
        return false;
    }
}
