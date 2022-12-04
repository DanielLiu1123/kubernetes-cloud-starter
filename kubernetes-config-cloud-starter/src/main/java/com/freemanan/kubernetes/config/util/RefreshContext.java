package com.freemanan.kubernetes.config.util;

import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;

/**
 * Helper class to get the Spring ApplicationContext and RefreshEvent when refreshing the context.
 *
 * @author Freeman
 */
public record RefreshContext(ApplicationContext applicationContext, RefreshEvent refreshEvent) {
    private static final ThreadLocal<RefreshContext> holder = new ThreadLocal<>();

    public static void set(RefreshContext refreshContext) {
        holder.set(refreshContext);
    }

    public static RefreshContext get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
