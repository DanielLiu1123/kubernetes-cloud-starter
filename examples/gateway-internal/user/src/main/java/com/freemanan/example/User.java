package com.freemanan.example;

import lombok.Data;

/**
 * @author Freeman
 */
@Data
public class User {
    private Long id;
    private String name;

    public static User of(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
}
