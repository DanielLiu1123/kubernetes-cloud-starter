package com.freemanan.kubernetes.grey.client.feign;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import feign.Client;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Freeman
 */
public class GreyClient implements Client {
    private static final Logger log = LoggerFactory.getLogger(GreyClient.class);

    private final Client delegate;

    public GreyClient(Client delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        List<Grey> greys = ThreadContext.greys();
        if (greys == null || greys.isEmpty()) {
            return delegate.execute(request, options);
        }
        // add header
        if (!request.headers().containsKey(GreyConst.HEADER_GREY_VERSION)) {
            request.header(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(greys));
        }
        // change url if needed
        URI origin = URI.create(request.url());
        URI newUri = GreyUtil.grey(origin, greys);
        if (Objects.equals(origin, newUri)) {
            return delegate.execute(request, options);
        }
        Request newRequest = copyRequest(request, newUri);
        if (log.isDebugEnabled()) {
            log.debug("[Grey] origin: {}, new: {}", origin, newUri);
        }
        return delegate.execute(newRequest, options);
    }

    private static Request copyRequest(Request request, URI newUri) {
        return Request.create(
                request.httpMethod(),
                newUri.toString(),
                request.headers(),
                request.body(),
                request.charset(),
                request.requestTemplate());
    }
}
