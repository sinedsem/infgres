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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class PusherService {

    private final JdbcTemplate jdbcTemplate;

    private final CloseableHttpClient httpClient;

    private final ObjectMapper mapper = new ObjectMapper();

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

        for (DatamineEntity entity : list) {
            long realEndTime = entity.getEndTime();

            while (entity.getStartTime() + interval <= realEndTime) {
                entity.setEndTime(entity.getStartTime() + interval * 2);
                pushOne(entity);
                entity.setStartTime(entity.getStartTime() + interval);
            }
        }

    }

    private void pushOne(DatamineEntity entity) {

        AgentReport agentReport = new AgentReport();
        agentReport.setId(null);

        agentReport.getEntities().add(entity);
        agentReport.setStartTime(entity.getStartTime());
        agentReport.setEndTime(entity.getEndTime());

        HttpPost request = new HttpPost("http://localhost:9010/listener/report");
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
