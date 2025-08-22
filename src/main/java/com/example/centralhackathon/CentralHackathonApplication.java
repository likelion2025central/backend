package com.example.centralhackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CentralHackathonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentralHackathonApplication.class, args);
    }

}
