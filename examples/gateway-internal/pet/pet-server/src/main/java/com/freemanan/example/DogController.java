package com.freemanan.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Freeman
 */
@RestController
@RequestMapping("/internal/v1/pet/dogs")
public class DogController implements DogApi {

    private final Map<Long, Dog> db;

    public DogController(@Value("${server.port}") int port) {
        this.db = Map.of(
                1L, Dog.of(1L, 1L, "Freeman's dog -> " + port),
                2L, Dog.of(2L, 2L, "Tom's dog -> " + port),
                3L, Dog.of(3L, 3L, "Jerry's dog -> " + port));
    }

    @Override
    @GetMapping
    public List<Dog> list() {
        return List.copyOf(db.values());
    }

    @Override
    @GetMapping("/{id}")
    public Dog get(Long id) {
        return db.get(id);
    }

    @Override
    @PostMapping("/search")
    public List<Dog> search(Dog dog) {
        if (dog == null) {
            return List.of();
        }
        return db.values().stream()
                .filter(d -> Optional.ofNullable(dog.getId())
                                .map(it -> it.equals(d.getId()))
                                .orElse(true)
                        && Optional.ofNullable(dog.getName())
                                .map(it -> it.equals(d.getName()))
                                .orElse(true)
                        && Optional.ofNullable(dog.getUserId())
                                .map(it -> it.equals(d.getUserId()))
                                .orElse(true))
                .collect(Collectors.toList());
    }
}
