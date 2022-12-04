package com.freemanan.kubernetes.config.util;

import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;

/**
 * Helper class to get the Spring ApplicationContext and RefreshEvent when refreshing the context.
 *
 * @author Freeman
 */
public class RefreshContext {
    private static final ThreadLocal<RefreshContext> holder = new ThreadLocal<>();

    private final ApplicationContext applicationContext;
    private final RefreshEvent refreshEvent;

    public RefreshContext(ApplicationContext applicationContext, RefreshEvent refreshEvent) {
        this.applicationContext = applicationContext;
        this.refreshEvent = refreshEvent;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public RefreshEvent getRefreshEvent() {
        return refreshEvent;
    }

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
