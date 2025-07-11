package com.example.eventplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.eventplanner")
public class EventPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventPlannerApplication.class, args);
    }
}