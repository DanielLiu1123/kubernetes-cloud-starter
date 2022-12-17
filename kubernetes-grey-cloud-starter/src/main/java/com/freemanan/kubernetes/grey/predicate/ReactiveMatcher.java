package com.freemanan.kubernetes.grey.predicate;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author Freeman
 */
public interface ReactiveMatcher {

    boolean match(ServerWebExchange exchange);
}
