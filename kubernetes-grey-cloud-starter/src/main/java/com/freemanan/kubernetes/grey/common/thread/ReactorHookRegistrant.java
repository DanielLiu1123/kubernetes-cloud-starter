package com.freemanan.kubernetes.grey.common.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import java.util.function.Function;
import reactor.core.scheduler.Schedulers;

/**
 * @author Freeman
 */
public class ReactorHookRegistrant {
    private static final String TTL_THREAD_LOCAL_SUPPORT = "TTL_THREAD_LOCAL_SUPPORT";

    /**
     * Register reactor hook to support thread local for {@link TransmittableThreadLocal}.
     */
    public static void registerThreadLocalSupport4TTL() {
        // see https://github.com/alibaba/transmittable-thread-local
        Function<Runnable, Runnable> decorator = runnable -> {
            return () -> TtlRunnable.get(runnable).run();
        };
        Schedulers.onScheduleHook(TTL_THREAD_LOCAL_SUPPORT, decorator);
    }

    /**
     * Deregister reactor hook to cancel support thread local for {@link TransmittableThreadLocal}.
     */
    public static void deregisterThreadLocalSupport4TTL() {
        Schedulers.resetOnScheduleHook(TTL_THREAD_LOCAL_SUPPORT);
    }
}
