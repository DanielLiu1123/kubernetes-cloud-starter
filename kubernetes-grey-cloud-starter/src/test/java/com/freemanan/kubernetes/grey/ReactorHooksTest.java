package com.freemanan.kubernetes.grey;

import static org.assertj.core.api.Assertions.assertThat;

import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Freeman
 */
public class ReactorHooksTest {

    static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    @Test
    public void testReactor_whenNoHooks() {
        USER_ID.set("freeman");

        String mainThread = Thread.currentThread().getName();

        Mono.just("hello")
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(__ -> {
                    assertThat(Thread.currentThread().getName()).isNotEqualTo(mainThread);
                    assertThat(USER_ID.get()).isNull();
                })
                .block();

        USER_ID.remove();
    }

    @Test
    public void testReactor_whenHasHook_thenPassThreadLocal() {
        USER_ID.set("freeman");

        String mainThread = Thread.currentThread().getName();

        doInHook(() -> {
            Mono.just("hello")
                    .delayElement(Duration.ofMillis(100))
                    .doOnNext(__ -> {
                        assertThat(Thread.currentThread().getName()).isNotEqualTo(mainThread);
                        assertThat(USER_ID.get()).isEqualTo("freeman");
                    })
                    .block();

            Flux.just("hello", "world")
                    .delayElements(Duration.ofMillis(100))
                    .doOnNext(__ -> {
                        assertThat(Thread.currentThread().getName()).isNotEqualTo(mainThread);
                        assertThat(USER_ID.get()).isEqualTo("freeman");
                    })
                    .collectList()
                    .block();
        });

        assertThat(USER_ID.get()).isEqualTo("freeman");
        USER_ID.remove();
    }

    private static void doInHook(Runnable action) {
        // register hook
        Schedulers.onScheduleHook(ThreadContext.class.getName(), runnable -> {
            String userId = USER_ID.get();
            return () -> {
                USER_ID.set(userId);
                try {
                    runnable.run();
                } finally {
                    USER_ID.remove();
                }
            };
        });

        // run action
        action.run();

        // remove hook
        Schedulers.resetOnScheduleHook(ThreadContext.class.getName());
    }
}
