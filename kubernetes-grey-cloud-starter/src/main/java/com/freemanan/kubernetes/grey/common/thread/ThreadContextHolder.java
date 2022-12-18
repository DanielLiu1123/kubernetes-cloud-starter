package com.freemanan.kubernetes.grey.common.thread;

/**
 * @author Freeman
 */
public class ThreadContextHolder {
    private static final ThreadLocal<ThreadContext> holder = new ThreadLocal<>();

    public static void set(ThreadContext context) {
        holder.set(context);
    }

    public static ThreadContext get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
