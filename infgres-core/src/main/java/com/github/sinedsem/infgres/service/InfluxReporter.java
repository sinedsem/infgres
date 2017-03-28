package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
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


    public ServerReport makeReport(Collection<UUID> nodeIds, Class<? extends DatamineEntity> clazz, long startTime, long endTime) {

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

        Query query = new Query("SELECT * FROM disk_status " + where.toString(), dbName);
        QueryResult result = influxDB.query(query, TimeUnit.SECONDS);

        ServerReport serverReport = new ServerReport();

        if (result == null || result.getResults() == null || result.getResults().isEmpty() ||
                result.getResults().get(0).getSeries() == null || result.getResults().get(0).getSeries().isEmpty()) {
            return serverReport;
        }

        QueryResult.Series series = result.getResults().get(0).getSeries().get(0);
        int totalSpaceIndex = series.getColumns().indexOf("totalSpace");
        int usedSpaceIndex = series.getColumns().indexOf("usedSpace");
        int numberIndex = series.getColumns().indexOf("number");
        int startTimeIndex = series.getColumns().indexOf("time");
        int endTimeIndex = series.getColumns().indexOf("endTime");
        int nodeIdIndex = series.getColumns().indexOf("nodeId");

        Map<UUID, NodeEntities> nodeEntitiesMap = new HashMap<>();

        for (List<Object> list : series.getValues()) {

            DiskStatus diskStatus = new DiskStatus();
            diskStatus.setNodeId(UUID.fromString((String) list.get(nodeIdIndex)));
            diskStatus.setStartTime((long) ((Double) list.get(startTimeIndex)).doubleValue());
            diskStatus.setEndTime((long) ((Double) list.get(endTimeIndex)).doubleValue());
            diskStatus.setTotalSpace((long) ((Double) list.get(totalSpaceIndex)).doubleValue());
            diskStatus.setUsedSpace((long) ((Double) list.get(usedSpaceIndex)).doubleValue());
            diskStatus.setNumber(Integer.parseInt((String) list.get(numberIndex)));

            NodeEntities nodeEntities;
            if (nodeEntitiesMap.containsKey(diskStatus.getNodeId())) {
                nodeEntities = nodeEntitiesMap.get(diskStatus.getNodeId());
            } else {
                nodeEntities = new NodeEntities();
                nodeEntities.setNodeId(diskStatus.getNodeId());
                nodeEntitiesMap.put(diskStatus.getNodeId(), nodeEntities);
            }
            nodeEntities.getEntities().add(diskStatus);
        }

        for (NodeEntities nodeEntities : nodeEntitiesMap.values()) {
            Utils.seizeEntities(nodeEntities.getEntities());
            serverReport.getNodeEntities().add(nodeEntities);
        }

        return serverReport;
    }

}
