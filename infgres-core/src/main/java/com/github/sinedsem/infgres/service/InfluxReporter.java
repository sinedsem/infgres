package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.utils.Utils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class InfluxReporter {


    @Value("${influx.dbName}")
    private String dbName;
    private final InfluxDB influxDB;


    @Autowired
    public InfluxReporter(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }


    public Map<UUID, NodeEntities> makeReportConfiguration(Collection<UUID> nodeIds, long startTime, long endTime) {

        StringBuilder where = buildWhere(nodeIds, startTime, endTime);

        Query query = new Query("SELECT * FROM backup_configuration " + where.toString(), dbName);
        QueryResult result = influxDB.query(query, TimeUnit.SECONDS);

        if (result == null || result.getResults() == null || result.getResults().isEmpty() ||
                result.getResults().get(0).getSeries() == null || result.getResults().get(0).getSeries().isEmpty()) {
            return Collections.emptyMap();
        }

        QueryResult.Series series = result.getResults().get(0).getSeries().get(0);
        int pathIndex = series.getColumns().indexOf("path");
        int levelIndex = series.getColumns().indexOf("level");
        int scheduleIndex = series.getColumns().indexOf("schedule");
        int startTimeIndex = series.getColumns().indexOf("time");
        int endTimeIndex = series.getColumns().indexOf("endTime");
        int nodeIdIndex = series.getColumns().indexOf("nodeId");

        Map<UUID, NodeEntities> nodeEntitiesMap = new HashMap<>();

        for (List<Object> list : series.getValues()) {

            BackupConfiguration backupConfiguration = new BackupConfiguration();
            backupConfiguration.setNodeId(UUID.fromString((String) list.get(nodeIdIndex)));
            backupConfiguration.setStartTime((long) ((Double) list.get(startTimeIndex)).doubleValue());
            backupConfiguration.setEndTime((long) ((Double) list.get(endTimeIndex)).doubleValue());
            backupConfiguration.setSchedule((String) list.get(scheduleIndex));
            backupConfiguration.setPath((String) list.get(pathIndex));
            backupConfiguration.setLevel((String) list.get(levelIndex));

            addToMap(nodeEntitiesMap, backupConfiguration);
        }

        for (NodeEntities nodeEntities : nodeEntitiesMap.values()) {
            Utils.seizeEntities(nodeEntities.getEntities());
        }

        return nodeEntitiesMap;
    }

    public Map<UUID, NodeEntities> makeReportJob(Collection<UUID> nodeIds, long startTime, long endTime) {

        StringBuilder where = buildWhere(nodeIds, startTime, endTime);

        Query query = new Query("SELECT * FROM backup_job " + where.toString(), dbName);
        QueryResult result = influxDB.query(query, TimeUnit.SECONDS);

        if (result == null || result.getResults() == null || result.getResults().isEmpty() ||
                result.getResults().get(0).getSeries() == null || result.getResults().get(0).getSeries().isEmpty()) {
            return Collections.emptyMap();
        }

        QueryResult.Series series = result.getResults().get(0).getSeries().get(0);
        int startTimeIndex = series.getColumns().indexOf("time");
        int endTimeIndex = series.getColumns().indexOf("endTime");
        int nodeIdIndex = series.getColumns().indexOf("nodeId");
        int pathIndex = series.getColumns().indexOf("path");
        int levelIndex = series.getColumns().indexOf("level");
        int errorCodeIndex = series.getColumns().indexOf("errorCode");
        int jobSizeIndex = series.getColumns().indexOf("jobSize");
        int statusIndex = series.getColumns().indexOf("status");

        Map<UUID, NodeEntities> nodeEntitiesMap = new HashMap<>();

        for (List<Object> list : series.getValues()) {

            BackupJob backupJob = new BackupJob();
            backupJob.setNodeId(UUID.fromString((String) list.get(nodeIdIndex)));
            backupJob.setStartTime((long) ((Double) list.get(startTimeIndex)).doubleValue());
            backupJob.setEndTime((long) ((Double) list.get(endTimeIndex)).doubleValue());
            backupJob.setPath((String) list.get(pathIndex));
            backupJob.setLevel((String) list.get(levelIndex));
            backupJob.setErrorCode((int) ((Double) list.get(errorCodeIndex)).doubleValue());
            backupJob.setJobSize((int) ((Double) list.get(jobSizeIndex)).doubleValue());
            backupJob.setStatus((String) list.get(statusIndex));

            addToMap(nodeEntitiesMap, backupJob);
        }

        for (NodeEntities nodeEntities : nodeEntitiesMap.values()) {
            Utils.seizeEntities(nodeEntities.getEntities());
        }

        return nodeEntitiesMap;
    }

    private void addToMap(Map<UUID, NodeEntities> nodeEntitiesMap, DatamineEntity entity) {
        NodeEntities nodeEntities;
        if (nodeEntitiesMap.containsKey(entity.getNodeId())) {
            nodeEntities = nodeEntitiesMap.get(entity.getNodeId());
        } else {
            nodeEntities = new NodeEntities();
            nodeEntities.setNodeId(entity.getNodeId());
            nodeEntitiesMap.put(entity.getNodeId(), nodeEntities);
        }
        nodeEntities.getEntities().add(entity);
    }

    private StringBuilder buildWhere(Collection<UUID> nodeIds, long startTime, long endTime) {
        StringBuilder where = new StringBuilder().append("WHERE time >= ").append(startTime).append("s AND time <= ").append(endTime).append("s");

        if (!nodeIds.isEmpty()) {
            where.append(" AND (");
            int i = 0;
            for (UUID nodeId : nodeIds) {
                if (i > 0) {
                    where.append(" OR ");
                }
                where.append("nodeId = '");
                where.append(nodeId.toString());
                where.append("'");
                i++;
            }
            where.append(")");
        }
        return where;
    }

}
