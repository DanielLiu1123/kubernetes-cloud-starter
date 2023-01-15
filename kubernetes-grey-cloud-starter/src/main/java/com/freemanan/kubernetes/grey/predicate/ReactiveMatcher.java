package com.freemanan.kubernetes.grey.predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author Freeman
 */
public interface ReactiveMatcher {

    boolean match(ServerHttpRequest request);
}
