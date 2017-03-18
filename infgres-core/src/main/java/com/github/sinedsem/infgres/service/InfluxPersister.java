package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class InfluxPersister {

    private static final String RETENTION = "autogen";
    private final ExecutorService persisterExecutor;

    @Value("${influx.dbName}")
    private String dbName;

    private volatile Set<Point> entitiesToPersist = ConcurrentHashMap.newKeySet();
    private Lock lock = new ReentrantLock();

    private final InfluxDB influxDB;
    private final PostgresPersister postgresPersister;
    private final ExecutorService nodesExecutor;
    private final AtomicLong endTime;


    @Autowired
    public InfluxPersister(PostgresPersister postgresPersister, InfluxDB influxDB, ExecutorService nodesExecutor, @Qualifier("endTime") AtomicLong endTime) {
        this.postgresPersister = postgresPersister;
        this.influxDB = influxDB;
        this.nodesExecutor = nodesExecutor;
        this.endTime = endTime;
        this.persisterExecutor = Executors.newSingleThreadExecutor();
    }

    boolean persist(AgentReport agentReport) {

        for (DatamineEntity entity : agentReport.getEntities()) {
            nodesExecutor.submit(() -> postgresPersister.createNodeIfNotExists(entity.getNodeId()), null);

            Point.Builder builder = Point.measurement(entity.getInfluxMeasurement())
                    .time(entity.getStartTime(), TimeUnit.SECONDS)
                    .tag("nodeId", entity.getNodeId().toString())
                    .addField("requestId", agentReport.getId().toString());
            entity.setInfluxTagsAndFields(builder);

            entitiesToPersist.add(builder.build());
        }

        // executing in another thread to release http request
        persisterExecutor.submit(() -> {
            if (lock.tryLock()) {
                try {
                    doPersist();
                } finally {
                    lock.unlock();
                }
            }
        });

        return true;
    }

    private void doPersist() {
        while (!entitiesToPersist.isEmpty()) {

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
        endTime.set(System.nanoTime());

    }

}
