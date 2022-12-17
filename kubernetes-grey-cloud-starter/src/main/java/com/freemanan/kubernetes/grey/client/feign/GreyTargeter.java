package com.freemanan.kubernetes.grey.client.feign;

import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.util.GreyUtil;
import com.freemanan.kubernetes.grey.thread.ThreadContext;
import com.freemanan.kubernetes.grey.thread.ThreadContextHolder;
import feign.Feign;
import feign.Request;
import feign.RequestTemplate;
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
        return feign.target(new GreyTarget<>(target));
    }

    static class GreyTarget<T> implements Target<T> {

        private final Target<T> delegate;

        public GreyTarget(Target<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Class<T> type() {
            return delegate.type();
        }

        @Override
        public String name() {
            return delegate.name();
        }

        @Override
        public String url() {
            String originUrl = delegate.url();

            ThreadContext threadContext = ThreadContextHolder.get();
            if (threadContext == null) {
                return originUrl;
            }
            List<Grey> greys = threadContext.getGreys();
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

        @Override
        public Request apply(RequestTemplate input) {
            return delegate.apply(input);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GreyTarget<?> that = (GreyTarget<?>) o;
            return Objects.equals(delegate, that.delegate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegate);
        }

        @Override
        public String toString() {
            return "DynamicUrlTarget{" + "delegate=" + delegate + '}';
        }
    }
}
