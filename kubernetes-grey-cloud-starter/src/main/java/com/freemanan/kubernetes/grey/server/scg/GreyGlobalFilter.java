package com.freemanan.kubernetes.grey.server.scg;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freemanan.kubernetes.grey.Ctx;
import com.freemanan.kubernetes.grey.Grey;
import com.freemanan.kubernetes.grey.GreyApi;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.Target;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
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

    private final GreyApi greyApi;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String greyVersion = exchange.getRequest().getHeaders().getFirst(GreyConst.HEADER_GREY_VERSION);
        Mono<Map<String, List<Target>>> routeMap =
                StringUtils.hasText(greyVersion) ? parseRouteFromString(greyVersion) : getMatchedRouteFromApi(exchange);

        return routeMap.flatMap(rules -> {
                    ServerWebExchange headerAdded = addGreyVersionHeader(exchange, rules);
                    URI origin = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
                    URI newUri = GreyUtil.grey(origin, rules);
                    if (Objects.equals(origin, newUri)) {
                        return chain.filter(headerAdded);
                    }
                    ServerWebExchange uriChanged = headerAdded
                            .mutate()
                            .request(headerAdded
                                    .getRequest()
                                    .mutate()
                                    .uri(newUri)
                                    .build())
                            .build();
                    uriChanged.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUri);
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "[Grey] origin: {}, new: {}",
                                exchange.getRequest().getURI(),
                                newUri);
                    }
                    return chain.filter(uriChanged);
                })
                // request grey gateway failed, fallback to normal request
                .onErrorResume(WebClientRequestException.class, e -> {
                    log.error("[Grey] Request grey gateway failed", e);
                    return chain.filter(exchange);
                })
                // no matched grey rule, nothing to do
                .switchIfEmpty(chain.filter(exchange));
    }

    @NotNull
    private static Mono<Map<String, List<Target>>> parseRouteFromString(String greyVersion) {
        return Mono.just(JsonUtil.toBean(greyVersion, new TypeReference<Map<String, List<Target>>>() {}))
                .onErrorResume(e -> {
                    // error format, no need to pass to downstream
                    log.error("[Grey] parse grey version header error, value: {}", greyVersion, e);
                    return Mono.empty();
                });
    }

    /**
     * @return possible empty mono, if no matched grey rule
     */
    @NotNull
    private Mono<Map<String, List<Target>>> getMatchedRouteFromApi(ServerWebExchange exchange) {
        EvaluationContext ec = evaluationContext(exchange);
        return greyApi.findAll().flatMap(greys -> firstMatchedGrey(ec, greys)).map(Grey::getRules);
    }

    private static EvaluationContext evaluationContext(ServerWebExchange exchange) {
        Map<String, String> headers = exchange.getRequest().getHeaders().toSingleValueMap();
        long timeMs = System.currentTimeMillis();
        Ctx rootObject = new Ctx(
                Map.copyOf(headers), timeMs, new SimpleDateFormat(Ctx.TIME_STR_FORMAT).format(new Date(timeMs)));
        return new StandardEvaluationContext(rootObject);
    }

    private Mono<Grey> firstMatchedGrey(EvaluationContext ctx, List<Grey> greys) {
        return Flux.fromIterable(greys).filter(grey -> match(ctx, grey)).take(1).singleOrEmpty();
    }

    private boolean match(EvaluationContext ctx, Grey grey) {
        try {
            Boolean result = parser.parseExpression(grey.getPredicate()).getValue(ctx, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("[Grey] evaluate expression failed, condition: " + grey.getPredicate(), e);
            return false;
        }
    }

    private ServerWebExchange addGreyVersionHeader(ServerWebExchange exchange, Map<String, List<Target>> ruleMap) {
        if (!exchange.getRequest().getHeaders().containsKey(GreyConst.HEADER_GREY_VERSION)) {
            return exchange.mutate()
                    .request(exchange.getRequest()
                            .mutate()
                            .headers(headers -> headers.add(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(ruleMap)))
                            .build())
                    .build();
        }
        return exchange;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
