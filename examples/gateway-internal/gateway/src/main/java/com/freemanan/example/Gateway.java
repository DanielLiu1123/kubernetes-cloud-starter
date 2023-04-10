package com.freemanan.example;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
@SpringBootApplication
@RestController
public class Gateway {
    public static void main(String[] args) {
        SpringApplication.run(Gateway.class, args);
    }

    @GetMapping("/interval")
    public Flux<ServerSentEvent<Long>> interval() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> ServerSentEvent.builder(i)
                        .id(String.valueOf(i))
                        .comment("comment: " + i)
                        .build())
                .take(5);
    }

    @GetMapping("/interval2")
    public Mono<List<Map<String, Long>>> interval2() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> Map.of("count", System.currentTimeMillis()))
                .take(5)
                .collectList();
    }
}
