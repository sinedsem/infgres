package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import java.util.concurrent.TimeUnit;

public class InfluxContinuousRepositoryImpl<T extends ContinuousDatamineEntity> extends AbstractRepositoryImpl<T> implements ContinuousRepository<T> {
    private static final String RETENTION = "autogen";

    @Value("${influx.dbName}")
    private String dbName;

    private InfluxDB influxDB;

    public InfluxContinuousRepositoryImpl(Class<T> domainClass, EntityManager entityManager, InfluxDB influxDB) {
        super(domainClass, entityManager);
        this.influxDB = influxDB;
    }

    @Override
    public <S extends T> S save(S entity) {
        Point.Builder builder = Point.measurement(entity.getInfluxMeasurement())
                .time(entity.getStartTime(), TimeUnit.SECONDS)
                .tag("nodeId", entity.getNodeId().toString())
                .addField("requestId", entity.getRequestId().toString());
        entity.setInfluxTagsAndFields(builder);
        Point point = builder.build();
        influxDB.write(dbName, RETENTION, point);
        return entity;
    }

    @Override
    public T getPrevious(T entity) {
        return null;
    }

    @Override
    public T getNext(T entity) {
        return null;
    }
}
