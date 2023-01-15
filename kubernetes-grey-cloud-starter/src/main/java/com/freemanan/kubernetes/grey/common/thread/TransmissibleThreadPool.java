package com.freemanan.kubernetes.grey.common.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Wrap {@link ThreadPoolExecutor} to transfer {@link ThreadLocal} context when switching threads.
 *
 * <p> Typical usage:
 * <pre>{@code
 * ThreadPoolExecutor executor = ...
 * ThreadPoolExecutor TransmissibleExecutor = TransmissibleThreadPool.wrap(executor);
 * }
 * </pre>
 *
 * @author Freeman
 */
public class TransmissibleThreadPool extends ThreadPoolExecutor {

    private TransmissibleThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public static ThreadPoolExecutor wrap(ThreadPoolExecutor executor) {
        if (executor instanceof TransmissibleThreadPool) {
            return executor;
        }
        return new TransmissibleThreadPool(
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getKeepAliveTime(TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS,
                executor.getQueue(),
                executor.getThreadFactory(),
                executor.getRejectedExecutionHandler());
    }

    @Override
    public void execute(Runnable command) {
        super.execute(TransmissibleRunnable.wrap(command));
    }

    @Override
    public boolean remove(Runnable task) {
        // need equals and hashCode method only compare the runnable
        return super.remove(TransmissibleRunnable.wrap(task));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return super.newTaskFor(TransmissibleRunnable.wrap(runnable), value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return super.newTaskFor(TransmissibleCallable.wrap(callable));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(TransmissibleRunnable.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(TransmissibleRunnable.wrap(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(TransmissibleCallable.wrap(task));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(tasks.stream().map(TransmissibleCallable::wrap).collect(Collectors.toList()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(
                tasks.stream().map(TransmissibleCallable::wrap).collect(Collectors.toList()), timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return super.invokeAll(tasks.stream().map(TransmissibleCallable::wrap).collect(Collectors.toList()));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return super.invokeAll(
                tasks.stream().map(TransmissibleCallable::wrap).collect(Collectors.toList()), timeout, unit);
    }
}
