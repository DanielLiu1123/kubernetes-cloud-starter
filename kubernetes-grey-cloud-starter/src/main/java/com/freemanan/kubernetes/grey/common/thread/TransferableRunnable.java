package com.freemanan.kubernetes.grey.common.thread;

import java.util.Objects;

/**
 * @author Freeman
 */
public class TransferableRunnable implements Runnable {

    private final Runnable runnable;
    private final ThreadContext threadContext;

    public TransferableRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.threadContext = ThreadContextHolder.get();
    }

    public static Runnable wrap(Runnable runnable) {
        return new TransferableRunnable(runnable);
    }

    @Override
    public void run() {
        ThreadContextHolder.set(threadContext);
        try {
            runnable.run();
        } finally {
            ThreadContextHolder.remove();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferableRunnable that = (TransferableRunnable) o;
        return Objects.equals(runnable, that.runnable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runnable);
    }
}
