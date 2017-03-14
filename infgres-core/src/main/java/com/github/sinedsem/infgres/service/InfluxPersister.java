package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class InfluxPersister {

    private static final String RETENTION = "autogen";

    @Value("${influx.dbName}")
    private String dbName;

    private final InfluxDB influxDB;

    private final PostgresPersister postgresPersister;

    @Autowired
    public InfluxPersister(PostgresPersister postgresPersister, InfluxDB influxDB) {
        this.postgresPersister = postgresPersister;
        this.influxDB = influxDB;
    }

    boolean persist(AgentReport agentReport) {

        BatchPoints batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .retentionPolicy(RETENTION)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (DatamineEntity entity : agentReport.getEntities()) {
            postgresPersister.createNodeIfNotExists(entity);

            Point.Builder builder = Point.measurement(entity.getInfluxMeasurement())
                    .time(entity.getStartTime(), TimeUnit.SECONDS)
                    .tag("nodeId", entity.getNodeId().toString())
                    .addField("requestId", agentReport.getId().toString());
            entity.setInfluxTagsAndFields(builder);
            batchPoints.point(builder.build());
        }

        influxDB.write(batchPoints);

        return true;
    }

}
