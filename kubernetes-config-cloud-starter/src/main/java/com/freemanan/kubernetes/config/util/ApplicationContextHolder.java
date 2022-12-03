package com.freemanan.kubernetes.config.util;

import java.util.concurrent.atomic.AtomicReference;
import org.springframework.context.ApplicationContext;

/**
 * Helper class to get the Spring ApplicationContext when refreshing the context.
 *
 * @author Freeman
 */
public class ApplicationContextHolder {

    private static final AtomicReference<ApplicationContext> context = new AtomicReference<>();

    public static synchronized void set(ApplicationContext applicationContext) {
        context.set(applicationContext);
    }

    public static synchronized ApplicationContext get() {
        return context.get();
    }

    public static synchronized void remove() {
        context.set(null);
    }
}
