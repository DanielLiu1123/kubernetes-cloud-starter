package com.freemanan.kubernetes.grey.common.thread;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author Freeman
 */
public class TransferableCallable<V> implements Callable<V> {

    private final Callable<V> callable;
    private final ThreadContext threadContext;

    public TransferableCallable(Callable<V> callable) {
        this.callable = callable;
        this.threadContext = ThreadContextHolder.get();
    }

    public static <V> Callable<V> wrap(Callable<V> callable) {
        return new TransferableCallable<>(callable);
    }

    @Override
    public V call() throws Exception {
        ThreadContextHolder.set(threadContext);
        try {
            return callable.call();
        } finally {
            ThreadContextHolder.remove();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferableCallable<?> that = (TransferableCallable<?>) o;
        return Objects.equals(callable, that.callable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callable);
    }
}
