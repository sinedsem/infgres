package com.github.sinedsem.infgres.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.sinedsem.infgres"})
public class InfgresCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfgresCoreApplication.class, args);
    }
}