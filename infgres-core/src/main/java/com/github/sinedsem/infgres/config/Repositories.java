package com.github.sinedsem.infgres.config;

import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.impl.AbstractRepositoryImpl;
import com.github.sinedsem.infgres.repository.impl.ContinuousRepositoryImpl;
import com.github.sinedsem.infgres.repository.impl.EventRepositoryImpl;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class Repositories {

    private boolean influx = true;

    @Bean
    public InfluxDB influxDB() {
        return InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root");
    }

    @Bean
    @Autowired
    public AbstractRepositoryImpl<BackupConfiguration> backupConfigurationRepositoryImpl(EntityManager entityManager) {
        return getContinuousImplementation(entityManager, BackupConfiguration.class);
    }

    @Bean
    @Autowired
    public AbstractRepositoryImpl<BackupJob> backupJobRepositoryImpl(EntityManager entityManager) {
        return getEventImplementation(entityManager, BackupJob.class);
    }

    private <T extends ContinuousDatamineEntity> AbstractRepositoryImpl<T> getContinuousImplementation(EntityManager entityManager, Class<T> clazz) {
        return new ContinuousRepositoryImpl<>(clazz, entityManager);
    }

    private <T extends EventDatamineEntity> AbstractRepositoryImpl<T> getEventImplementation(EntityManager entityManager, Class<T> clazz) {
        return new EventRepositoryImpl<>(clazz, entityManager);
    }

    public boolean isInflux() {
        return influx;
    }

    public void setInflux(boolean influx) {
        this.influx = influx;
    }
}
