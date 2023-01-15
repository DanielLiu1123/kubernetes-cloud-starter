package com.freemanan.kubernetes.grey.client.okhttp;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Freeman
 * @since 2023/1/9
 */
public class GreyInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(GreyInterceptor.class);

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        List<Grey> greys = ThreadContext.greys();
        if (greys == null || greys.isEmpty()) {
            return chain.proceed(request);
        }
        // add header
        Request.Builder builder = request.newBuilder();
        if (request.header(GreyConst.HEADER_GREY_VERSION) == null) {
            builder.addHeader(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(greys));
        }
        // change url if needed
        URI origin = request.url().uri();
        URI newUri = GreyUtil.grey(origin, greys);
        if (Objects.equals(origin, newUri)) {
            return chain.proceed(builder.build());
        }
        Request newRequest = builder.url(newUri.toURL()).build();
        if (log.isDebugEnabled()) {
            log.debug("[Grey] origin: {}, new: {}", origin, newUri);
        }
        return chain.proceed(newRequest);
    }
}
