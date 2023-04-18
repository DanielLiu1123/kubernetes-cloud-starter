package com.freemanan.example;

import com.freemanan.starter.httpexchange.EnableExchangeClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Freeman
 */
@SpringBootApplication
@EnableExchangeClients(clients = DogApi.class)
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }
}
