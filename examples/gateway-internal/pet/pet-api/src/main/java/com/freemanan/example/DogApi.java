package com.freemanan.example;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Freeman
 */
@FeignClient(name = "dog", url = "http://pet:8081", path = "/v1/pet/dogs")
public interface DogApi {

    @GetMapping
    List<Dog> list();

    @GetMapping("/{id}")
    Dog get(@PathVariable("id") Long id);

    @PostMapping("/search")
    List<Dog> search(@RequestBody Dog dog);
}
