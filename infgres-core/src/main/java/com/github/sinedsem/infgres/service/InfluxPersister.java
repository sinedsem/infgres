package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class InfluxPersister {

    private volatile Set<Point> entitiesToPersist = ConcurrentHashMap.newKeySet();

    private Lock lock = new ReentrantLock();

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


    private void doPersist() {
//        lock.lock();
//        try {
        while (!entitiesToPersist.isEmpty()) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BatchPoints batchPoints = BatchPoints
                    .database(dbName)
                    .tag("async", "false")
                    .retentionPolicy(RETENTION)
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .build();

            Set<Point> toPersist = this.entitiesToPersist;
            entitiesToPersist = ConcurrentHashMap.newKeySet();

            for (Point point : toPersist) {
                batchPoints.point(point);
            }

            System.out.println("persisting " + toPersist.size() + " points");

            influxDB.write(batchPoints);
        }
//        System.out.println("leaved while");
//        } finally {
//            lock.unlock();
//        }

    }

    boolean persist(AgentReport agentReport) {

        for (DatamineEntity entity : agentReport.getEntities()) {
            postgresPersister.createNodeIfNotExists(entity);

            Point.Builder builder = Point.measurement(entity.getInfluxMeasurement())
                    .time(entity.getStartTime(), TimeUnit.SECONDS)
                    .tag("nodeId", entity.getNodeId().toString())
                    .addField("requestId", agentReport.getId().toString());
            entity.setInfluxTagsAndFields(builder);

            entitiesToPersist.add(builder.build());
        }


//        System.out.println("try lock");
        if (lock.tryLock()) {
            try {
                //todo move to another thread to release 1st request
                doPersist();
            } finally {
                lock.unlock();
            }
        } else {
//            System.out.println("already locked");
        }


        return true;
    }

/*    boolean persist(DatamineEntity entity) {

        Point.Builder builder = Point.measurement(entity.getInfluxMeasurement())
                .time(entity.getStartTime(), TimeUnit.SECONDS)
                .tag("nodeId", entity.getNodeId().toString())
                .addField("requestId", entity.getRequestId().toString());
        entity.setInfluxTagsAndFields(builder);

        influxDB.write(dbName, RETENTION, builder.build());

        return true;
    }*/

}
