package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
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
        Query query = new Query("SELECT * FROM disk_status", dbName);
        QueryResult result = influxDB.query(query, TimeUnit.MILLISECONDS);

        QueryResult.Series series = result.getResults().get(0).getSeries().get(0);
        int totalSpaceIndex = series.getColumns().indexOf("totalSpace");
        int usedSpaceIndex = series.getColumns().indexOf("usedSpace");
        int numberIndex = series.getColumns().indexOf("number");
        int startTimeIndex = series.getColumns().indexOf("time");
        int endTimeIndex = series.getColumns().indexOf("endTime");
        int nodeIdIndex = series.getColumns().indexOf("nodeId");

        NodeEntities nodeEntities = new NodeEntities();

        for (List<Object> list : series.getValues()) {
            DiskStatus diskStatus = new DiskStatus();
            diskStatus.setNodeId(UUID.fromString((String) list.get(nodeIdIndex)));
            diskStatus.setStartTime((long) ((Double) list.get(startTimeIndex)).doubleValue());
            diskStatus.setEndTime((long) ((Double) list.get(endTimeIndex)).doubleValue());
            nodeEntities.getEntities().add(diskStatus);
        }

        ServerReport serverReport = new ServerReport();
        serverReport.getNodeEntities().add(nodeEntities);

        return serverReport;
    }
}
