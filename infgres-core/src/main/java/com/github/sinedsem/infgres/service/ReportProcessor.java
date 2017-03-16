package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProcessor {

    @Value("${influx}")
    private boolean influx;

    private final PostgresPersister postgresPersister;
    private final InfluxPersister influxPersister;

    @Autowired
    public ReportProcessor(PostgresPersister postgresPersister, InfluxPersister influxPersister) {
        this.postgresPersister = postgresPersister;
        this.influxPersister = influxPersister;
    }

    public void logRequestHistory(AgentReport agentReport) {
        if (agentReport.getId() == null) {
            agentReport.setId(UUID.randomUUID());
        }

        if (influx) {
            persistInflux(agentReport);
        } else {
            persistPostgres(agentReport);
        }
    }

    private void persistPostgres(AgentReport agentReport) {
        for (DatamineEntity datamineEntity : agentReport.getEntities()) {
            datamineEntity.setEndTime(agentReport.getEndTime());
            datamineEntity.setStartTime(agentReport.getStartTime());
            datamineEntity.setRequestId(agentReport.getId());
            postgresPersister.persist(datamineEntity);
        }
    }

    private void persistInflux(AgentReport agentReport) {

//        for (DatamineEntity entity : agentReport.getEntities()) {
//            entity.setRequestId(agentReport.getId());
//            influxPersister.persist(entity);
//        }
        influxPersister.persist(agentReport);
    }


}
