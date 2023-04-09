package com.freemanan.kubernetes.grey;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 * @since 2023/4/9
 */
public class ReactorTests {

    @Test
    void testMapNotNull() throws InterruptedException {
        Mono.just("hello")
                .mapNotNull(value -> null)
                .flatMap(value -> Mono.just(value + " world"))
                .switchIfEmpty(Mono.just("world222"))
                .subscribe(System.out::println);
        Thread.sleep(1000L);
    }

    @Test
    void testDefer() {
        Mono.justOrEmpty("")
                .switchIfEmpty(Mono.fromCallable(() -> {
                    System.out.println("hello world");
                    return "hello world";
                }))
                .subscribe(System.out::println);
    }
}
