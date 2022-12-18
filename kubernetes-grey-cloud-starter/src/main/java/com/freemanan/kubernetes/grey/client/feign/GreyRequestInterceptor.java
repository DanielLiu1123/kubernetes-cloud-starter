package com.freemanan.kubernetes.grey.client.feign;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Freeman
 * @since 2022/12/18
 */
public class GreyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        List<Grey> greys = ThreadContext.greys();
        if (greys == null) {
            return;
        }

        Map<String, Collection<String>> headers = template.headers();
        if (!headers.containsKey(GreyConst.HEADER_GREY_VERSION)) {
            template.header(GreyConst.HEADER_GREY_VERSION, JsonUtil.toJson(greys));
        }
    }
}
