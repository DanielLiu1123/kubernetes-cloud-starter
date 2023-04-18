package com.freemanan.example;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
@HttpExchange("/internal/v1/pet/dogs")
public interface DogApi {

    @GetExchange
    Mono<List<Dog>> list();

    @GetExchange("/{id}")
    Mono<Dog> get(@PathVariable("id") Long id);

    @PostExchange("/search")
    Mono<List<Dog>> search(@RequestBody Dog dog);
}
