package com.github.sinedsem.infgres.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class PusherService {

    private final JdbcTemplate jdbcTemplate;

    private final CloseableHttpClient httpClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private String host = "192.168.1.250";

    @Autowired
    public PusherService(CloseableHttpClient httpClient, JdbcTemplate jdbcTemplate) {
        this.httpClient = httpClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void push() {

        final long interval = jdbcTemplate.queryForObject("SELECT t as c FROM (SELECT (f_endtime - f_starttime) / 100 as t FROM dpa.view_group_config) times GROUP BY t HAVING COUNT(t) > 4 ORDER BY t, c LIMIT 1", Long.class) * 100;

        List<DatamineEntity> list = new ArrayList<>(10000);

        jdbcTemplate.query("SELECT f_inactivity, f_starttime, f_endtime, f_agent_id FROM dpa.view_group_config ORDER BY f_starttime", resultSet -> {
            DiskStatus diskStatus = new DiskStatus();
            diskStatus.setStartTime(resultSet.getLong("f_starttime"));
            diskStatus.setEndTime(resultSet.getLong("f_endtime"));
            diskStatus.setTotalSpace(resultSet.getLong("f_inactivity"));
            diskStatus.setNodeId(resultSet.getObject("f_agent_id", UUID.class));
            list.add(diskStatus);

        });


        List<DatamineEntity> result = new ArrayList<>(10000);

        for (DatamineEntity entity : list) {
            long realEndTime = entity.getEndTime();
            long startTime = entity.getStartTime();

            while (startTime + interval <= realEndTime) {
                DatamineEntity diskStatus = new DiskStatus((DiskStatus) entity);
                diskStatus.setEndTime(startTime + interval * 2);
                diskStatus.setStartTime(startTime);

                result.add(diskStatus);
                startTime = startTime + interval;
            }
        }

        System.out.println("pushing " + result.size());

        int batchSize = 100;

        for (int i = 0; i < result.size(); i += batchSize) {
            List<DatamineEntity> batch = new ArrayList<>(batchSize);
            for (int j = 0; j < batchSize && i + j < result.size(); j++) {
                batch.add(result.get(i + j));
            }
            new Thread(() -> pushAll(batch)).start();
        }

//        for (DatamineEntity entity : result) {
//            new Thread(() -> pushOne(entity)).start();
//        }

//        pushAll(result);

    }

    private void pushOne(DatamineEntity entity) {

        AgentReport agentReport = new AgentReport();
        agentReport.setId(null);

        agentReport.getEntities().add(entity);
//        agentReport.setStartTime(entity.getStartTime());
//        agentReport.setEndTime(entity.getEndTime());

        HttpPost request = new HttpPost("http://" + host + ":9010/listener/report");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectToJson(agentReport), "utf-8"));

//        System.out.println("sending report " + entity.getStartTime() + "-"+  entity.getEndTime());

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
//            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pushAll(Collection<? extends DatamineEntity> entities) {

        AgentReport agentReport = new AgentReport();
        agentReport.setId(null);

        agentReport.getEntities().addAll(entities);

        HttpPost request = new HttpPost("http://" + host + ":9010/listener/report");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectToJson(agentReport), "utf-8"));

//        System.out.println("sending report " + entity.getStartTime() + "-"+  entity.getEndTime());

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            response.close();
//            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Wrong object passed to ObjectMapper - can not convert to JSON", e);
        }
    }
}
