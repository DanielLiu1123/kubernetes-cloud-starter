package com.freemanan.example;

import lombok.Data;

/**
 * @author Freeman
 */
@Data
public class Dog {
    private Long id;
    private Long userId;
    private String name;

    public static Dog of(Long id, Long userId, String name) {
        Dog dog = new Dog();
        dog.setId(id);
        dog.setUserId(userId);
        dog.setName(name);
        return dog;
    }
}
