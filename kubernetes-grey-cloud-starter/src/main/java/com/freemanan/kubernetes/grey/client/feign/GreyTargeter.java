package com.freemanan.kubernetes.grey.client.feign;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import feign.Feign;
import feign.Target;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.Targeter;

/**
 * DynamicTargeter used to dynamically change the url of the Feign client.
 *
 * @author Freeman
 */
public class GreyTargeter implements Targeter {

    private static final Logger log = LoggerFactory.getLogger(GreyTargeter.class);

    @Override
    public <T> T target(
            FeignClientFactoryBean factory,
            Feign.Builder feign,
            FeignClientFactory context,
            Target.HardCodedTarget<T> target) {
        return feign.target(new GreyTarget<>(target.type(), target.name(), target.url()));
    }

    static class GreyTarget<T> extends Target.HardCodedTarget<T> {

        public GreyTarget(Class<T> type, String name, String url) {
            super(type, name, url);
        }

        @Override
        public String url() {
            String originUrl = super.url();

            List<Grey> greys = ThreadContext.greys();
            if (greys == null || greys.isEmpty()) {
                return originUrl;
            }
            URI origin = URI.create(originUrl);
            Grey grey = GreyUtil.getMathchedGrey(origin, greys);
            if (grey == null) {
                return originUrl;
            }
            URI newUri = GreyUtil.grey(origin, grey);

            if (Objects.equals(origin, newUri)) {
                return originUrl;
            }

            if (log.isDebugEnabled()) {
                log.debug("[FeignClient] Dynamic change url from {} to {}", origin, newUri);
            }
            return newUri.toString();
        }
    }
}
