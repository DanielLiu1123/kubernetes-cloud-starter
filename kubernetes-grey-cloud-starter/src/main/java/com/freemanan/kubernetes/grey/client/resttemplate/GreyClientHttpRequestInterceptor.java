package com.freemanan.kubernetes.grey.client.resttemplate;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.Context;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * @author Freeman
 */
public class GreyClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GreyClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        List<Grey> greys = Context.greys();
        if (greys == null || greys.isEmpty()) {
            return execution.execute(request, body);
        }
        // add header
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey(GreyConst.HEADER_GREY_VERSION)) {
            headers.add(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(greys));
        }
        // change url if needed
        URI origin = request.getURI();
        URI newUri = GreyUtil.grey(origin, greys);
        if (Objects.equals(origin, newUri)) {
            return execution.execute(request, body);
        }
        HttpRequest newRequest = new ReplaceUriHttpRequest(request, newUri);
        if (log.isDebugEnabled()) {
            log.debug("[Grey] origin: {}, new: {}", origin, newUri);
        }
        return execution.execute(newRequest, body);
    }

    private static final class ReplaceUriHttpRequest extends HttpRequestWrapper {
        private final URI uri;

        public ReplaceUriHttpRequest(HttpRequest request, URI newUri) {
            super(request);
            this.uri = newUri;
        }

        @Override
        public URI getURI() {
            return uri;
        }
    }
}
