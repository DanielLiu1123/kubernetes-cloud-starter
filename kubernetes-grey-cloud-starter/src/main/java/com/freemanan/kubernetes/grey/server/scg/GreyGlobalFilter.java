package com.freemanan.kubernetes.grey.server.scg;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freemanan.kubernetes.grey.Grey;
import com.freemanan.kubernetes.grey.GreyApi;
import com.freemanan.kubernetes.grey.KubernetesGreyProperties;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.Target;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import com.freemanan.kubernetes.grey.predicate.ReactiveMatcher;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
@RequiredArgsConstructor
public class GreyGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(GreyGlobalFilter.class);

    /**
     * Before {@link NettyRoutingFilter}, we can modify the request url before routing.
     */
    public static final int ORDER = NettyRoutingFilter.ORDER - 1;

    private final KubernetesGreyProperties properties;
    private final GreyApi greyApi;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        EvaluationContext ctx = new StandardEvaluationContext();
        return greyApi.findAll().mapNotNull(greys -> firstMatch(ctx, greys)).flatMap(grey -> {
            Map<String, List<Target>> ruleMap = JsonUtil.toBean(grey.rules(), new TypeReference<>() {});
            ServerWebExchange.Builder builder = exchange.mutate();
            // add header
            if (!exchange.getRequest().getHeaders().containsKey(GreyConst.HEADER_GREY_VERSION)) {
                builder.request(exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders ->
                                httpHeaders.add(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(grey.rules())))
                        .build());
            }
            // change url if needed
            URI origin = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
            URI newUri = GreyUtil.grey(origin, ruleMap);
            if (Objects.equals(origin, newUri)) {
                return chain.filter(builder.build());
            }
            builder.request(exchange.getRequest().mutate().uri(newUri).build());
            // url depends on 'GATEWAY_REQUEST_URL_ATTR', see NettyRoutingFilter
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUri);
            if (log.isDebugEnabled()) {
                log.debug("[Grey] origin: {}, new: {}", origin, newUri);
            }
            return chain.filter(builder.build());
        });
    }

    @Nullable
    private Grey firstMatch(EvaluationContext ctx, List<Grey> greys) {
        for (Grey grey : greys) {
            try {
                if (Boolean.TRUE.equals(parser.parseExpression(grey.predicate()).getValue(ctx, Boolean.class))) {
                    return grey;
                }
            } catch (ParseException | EvaluationException e) {
                log.warn("[Grey] parse condition failed, grey: {}", grey.predicate(), e);
            }
        }
        return null;
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
        return matcher.match(exchange.getRequest());
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
