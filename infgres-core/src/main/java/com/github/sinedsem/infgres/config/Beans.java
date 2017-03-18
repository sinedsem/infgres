package com.github.sinedsem.infgres.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class Beans {

    @Bean
    public AtomicLong startTime() {
        return new AtomicLong(-1);
    }

    @Bean
    public AtomicInteger requestCounter() {
        return new AtomicInteger(0);
    }

    @Bean
    public AtomicLong endTime() {
        return new AtomicLong(-1);
    }

    @Bean(destroyMethod = "shutdown")
    ExecutorService nodesExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
