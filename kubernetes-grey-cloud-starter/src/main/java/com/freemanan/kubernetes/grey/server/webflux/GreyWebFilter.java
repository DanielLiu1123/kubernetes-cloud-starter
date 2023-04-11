package com.freemanan.kubernetes.grey.server.webflux;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.Target;
import com.freemanan.kubernetes.grey.common.thread.Context;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux filter for grey.
 *
 * @author Freeman
 */
public class GreyWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(GreyWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String greyVersion = exchange.getRequest().getHeaders().getFirst(GreyConst.HEADER_GREY_VERSION);
        if (!StringUtils.hasText(greyVersion)) {
            return chain.filter(exchange);
        }
        Map<String, List<Target>> greys;
        try {
            greys = JsonUtil.toBean(greyVersion, new TypeReference<Map<String, List<Target>>>() {});
        } catch (Exception e) {
            // Json parse error, don't fail the request, but can't do grey
            log.warn("[Grey] Grey header JSON parse error, value: {}", greyVersion);
            return chain.filter(exchange);
        }
        Context.set(new Context(greys));
        try {
            return chain.filter(exchange);
        } finally {
            Context.remove();
        }
    }
}
