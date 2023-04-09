package com.freemanan.kubernetes.grey;

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
    @GetExchange("/v1/greys")
    Mono<List<Grey>> findAll();
}
