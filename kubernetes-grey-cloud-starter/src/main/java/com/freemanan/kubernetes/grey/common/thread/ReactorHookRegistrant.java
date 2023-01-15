package com.freemanan.kubernetes.grey.common.thread;

import reactor.core.scheduler.Schedulers;

/**
 * @author Freeman
 */
public class ReactorHookRegistrant {

    /**
     * Register reactor hook to support ThreadLocal.
     */
    public static void registerThreadLocalSupport() {
        Schedulers.onScheduleHook(ThreadContext.class.getName(), runnable -> {
            ThreadContext context = ThreadContextHolder.get();
            return () -> {
                ThreadContextHolder.set(context);
                try {
                    runnable.run();
                } finally {
                    ThreadContextHolder.remove();
                }
            };
        });
    }
}
