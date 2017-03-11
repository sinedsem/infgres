package com.github.sinedsem.infgres.config;

import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.impl.*;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
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

    @Bean
    public InfluxDB influxDB() {
        if (influx) {
            return InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root");
        }
        return null;
    }

    @Bean
    @Autowired
    public AbstractRepositoryImpl<DiskStatus> diskStatusRepositoryImpl(EntityManager entityManager, InfluxDB influxDB) {
        return getContinuousImplementation(entityManager, DiskStatus.class, influxDB);
    }

    @Bean
    @Autowired
    public AbstractRepositoryImpl<BackupJob> backupJobRepositoryImpl(EntityManager entityManager) {
        return getEventImplementation(entityManager, BackupJob.class);
    }

    private <T extends ContinuousDatamineEntity> AbstractRepositoryImpl<T> getContinuousImplementation(EntityManager entityManager, Class<T> clazz, InfluxDB influxDB) {
        if (influx) {
            return new InfluxContinuousRepositoryImpl<>(clazz, entityManager, influxDB);
        }
        return new PostgresContinuousRepositoryImpl<>(clazz, entityManager);
    }

    private <T extends EventDatamineEntity> AbstractRepositoryImpl<T> getEventImplementation(EntityManager entityManager, Class<T> clazz) {
        if (influx) {
            return new InfluxEventRepositoryImpl<>(clazz, entityManager);
        }
        return new PostgresEventRepositoryImpl<>(clazz, entityManager);
    }


}
