package com.makeupnow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MakeupnowBackendApplication {

    public static void main(String[] args) {
        // Charger le .env AVANT de lancer Spring Boot
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(MakeupnowBackendApplication.class, args);
    }
}
