package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProcessor {

    private final PostgresPersister postgresPersister;
    private final InfluxPersister influxPersister;
    private final Repositories repositories;


    @Autowired
    public ReportProcessor(PostgresPersister postgresPersister, InfluxPersister influxPersister, @Qualifier("repositories") Repositories repositories) {
        this.postgresPersister = postgresPersister;
        this.influxPersister = influxPersister;
        this.repositories = repositories;
    }

    public void processReport(AgentReport agentReport) {
        if (agentReport.getId() == null) {
            agentReport.setId(UUID.randomUUID());
        }

        if (repositories.isInflux()) {
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
