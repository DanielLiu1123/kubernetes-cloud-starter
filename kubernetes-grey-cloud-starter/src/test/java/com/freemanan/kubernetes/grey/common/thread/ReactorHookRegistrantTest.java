package com.freemanan.kubernetes.grey.common.thread;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * {@link ReactorHookRegistrant} tester.
 *
 * @author Freeman
 */
class ReactorHookRegistrantTest {

    /**
     * {@link ReactorHookRegistrant#registerThreadLocalSupport4TTL()}
     */
    @Test
    void registerThreadLocalSupport() throws InterruptedException {
        String foo = "foo";
        Ctx.set(new Ctx(foo));

        ReactorHookRegistrant.registerThreadLocalSupport4TTL();

        List<Boolean> result = new ArrayList<>();

        ExecutorService es = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));
        // create 2 threads first
        es.submit(() -> {
            Thread.sleep(100L);
            return null;
        });
        es.submit(() -> {
            Thread.sleep(100L);
            return null;
        });

        // 1. new thread
        new Thread(() -> {
                    result.add(Ctx.get().value().equals(foo));
                })
                .start();

        // 2. Reactor
        Mono.just("")
                .publishOn(Schedulers.parallel())
                .map(value -> {
                    result.add(Ctx.get().value().equals(foo));
                    return value;
                })
                .subscribe();

        // 3. Reactor + new thread
        new Thread(() -> {
                    Mono.just("")
                            .publishOn(Schedulers.parallel())
                            .map(value -> {
                                result.add(Ctx.get().value().equals(foo));
                                return value;
                            })
                            .subscribe();
                })
                .start();

        // 4. new thread + Reactor + new thread
        new Thread(() -> {
                    Mono.just("")
                            .publishOn(Schedulers.parallel())
                            .map(value -> {
                                new Thread(() -> {
                                            result.add(Ctx.get().value().equals(foo));
                                        })
                                        .start();
                                return value;
                            })
                            .subscribe();
                })
                .start();

        // 5. change context value + use pooled thread
        Ctx.set(new Ctx("bar"));
        es.submit(() -> {
            result.add(Ctx.get().value().equals("bar"));
        });

        // 6. remove context value + use pooled thread
        Ctx.remove();
        es.submit(() -> {
            result.add(Ctx.get() == null);
        });

        // 7/8. set context value in sub thread, child thread should see it, main thread should not see it
        es.submit(() -> {
            Ctx.set(new Ctx("foo"));
            new Thread(() -> {
                        result.add(Ctx.get().value().equals("foo"));
                    })
                    .start();
        });
        result.add(Ctx.get() == null);

        // 9/10. pooled threads not hold context value
        es.submit(() -> {
            result.add(Ctx.get() == null);
            Thread.sleep(100);
            return null;
        });
        es.submit(() -> {
            result.add(Ctx.get() == null);
            Thread.sleep(100);
            return null;
        });

        Thread.sleep(3000L);
        assertThat(result).hasSize(10);
        assertThat(result).doesNotContain(false);
    }

    private record Ctx(String value) {
        private static final ThreadLocal<Ctx> HOLDER = new TransmittableThreadLocal<>();

        public static Ctx get() {
            return HOLDER.get();
        }

        public static void set(Ctx context) {
            HOLDER.set(context);
        }

        public static void remove() {
            HOLDER.remove();
        }
    }
}
