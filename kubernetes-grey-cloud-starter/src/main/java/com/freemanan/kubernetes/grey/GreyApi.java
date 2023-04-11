package com.freemanan.kubernetes.grey;

import java.net.URI;
import java.util.List;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
public interface GreyApi {
    /**
     * Find all available greys, ordered by priority and created time.
     */
    @GetExchange
    Mono<List<Grey>> findAll(URI uri);
}
