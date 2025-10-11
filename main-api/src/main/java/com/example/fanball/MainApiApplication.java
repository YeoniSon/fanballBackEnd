package com.example.fanball;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.example.fanball")
@EnableJpaRepositories(basePackages = "com.example.fanball")
@EntityScan(basePackages = "com.example.fanball")
public class MainApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApiApplication.class, args);
    }
}
