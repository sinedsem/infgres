package com.github.sinedsem.infgres.config;

import com.github.sinedsem.infgres.datamodel.datamine.Battery;
import com.github.sinedsem.infgres.repository.impl.InfluxPostgresRepositoryImpl;
import com.github.sinedsem.infgres.repository.impl.PostgresRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class Repositories {

    @Value("${influx}")
    private boolean influx;

    @Bean(name = "batteryRepositoryImpl")
    @Autowired
    public PostgresRepositoryImpl<Battery> batteryRepositoryImpl(EntityManager entityManager) {
        return getImplementation(entityManager, Battery.class);
    }

    private PostgresRepositoryImpl<Battery> getImplementation(EntityManager entityManager, Class<Battery> clazz) {
        if (influx) {
            return new InfluxPostgresRepositoryImpl<>(clazz, entityManager);
        }
        return new PostgresRepositoryImpl<>(clazz, entityManager);
    }


}
