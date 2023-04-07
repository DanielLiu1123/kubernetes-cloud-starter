package com.freemanan.kubernetes.grey.client.webclient;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.Context;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
public class GreyExchangeFilterFunction implements ExchangeFilterFunction {
    private static final Logger log = LoggerFactory.getLogger(GreyExchangeFilterFunction.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        List<Grey> greys = Context.greys();
        if (greys == null || greys.isEmpty()) {
            return next.exchange(request);
        }
        // add header
        ClientRequest.Builder builder = ClientRequest.from(request).headers(httpHeaders -> {
            if (!httpHeaders.containsKey(GreyConst.HEADER_GREY_VERSION)) {
                httpHeaders.add(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(greys));
            }
        });
        // change url if needed
        URI origin = URI.create(request.url().toString());
        URI newUri = GreyUtil.grey(origin, greys);
        if (Objects.equals(origin, newUri)) {
            return next.exchange(builder.build());
        }
        ClientRequest newRequest = builder.url(newUri).build();
        if (log.isDebugEnabled()) {
            log.debug("[Grey] origin: {}, new: {}", origin, newUri);
        }
        return next.exchange(newRequest);
    }
}
