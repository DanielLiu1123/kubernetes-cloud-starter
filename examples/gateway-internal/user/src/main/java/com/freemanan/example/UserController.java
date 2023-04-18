package com.freemanan.example;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 */
@RestController
@RequestMapping("/v1/user/users")
@RequiredArgsConstructor
public class UserController {

    private static final Map<Long, User> db = Map.of(
            1L, User.of(1L, "Freeman"),
            2L, User.of(2L, "Tom"),
            3L, User.of(3L, "Jerry"));

    private final DogApi dogApi;

    @GetMapping
    public List<User> list() {
        return List.copyOf(db.values());
    }

    @GetMapping("/{id}")
    public Mono<User> get(@PathVariable Long id) {
        return Mono.justOrEmpty(db.get(id));
    }

    @GetMapping("/{userId}/dogs")
    public Mono<List<Dog>> listDogs(@PathVariable Long userId) {
        return dogApi.search(Dog.of(null, userId, null));
    }
}
