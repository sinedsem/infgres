package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProcessor {

    private final PersisterService persisterService;

    @Autowired
    public ReportProcessor(PersisterService persisterService) {
        this.persisterService = persisterService;
    }

    public void logRequestHistory(AgentReport agentReport) {
        if (agentReport.getRequestHistoryId() == null) {
            agentReport.setRequestHistoryId(UUID.randomUUID());
        }

        for (DatamineEntity datamineEntity : agentReport.getEntities()) {
            datamineEntity.setEndTime(agentReport.getEndTime());
            datamineEntity.setStartTime(agentReport.getStartTime());
            datamineEntity.setRequestId(agentReport.getRequestHistoryId());
            persisterService.persist(datamineEntity);
        }

    }
}
