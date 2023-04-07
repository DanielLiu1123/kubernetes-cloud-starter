package com.freemanan.kubernetes.grey.common.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import reactor.core.scheduler.Schedulers;

/**
 * @author Freeman
 */
public class ReactorHookRegistrant {
    private static final String TTL_THREAD_LOCAL_SUPPORT = "TTL_THREAD_LOCAL_SUPPORT";

    /**
     * Register reactor hook to support ThreadLocal.
     */
    public static <T> void registerThreadLocalSupport(
            Class<T> clz, Supplier<T> getter, Consumer<T> setter, Runnable remover) {
        Schedulers.onScheduleHook(clz.getName(), runnable -> {
            T context = getter.get();
            return () -> {
                setter.accept(context);
                try {
                    runnable.run();
                } finally {
                    remover.run();
                }
            };
        });
    }

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
