package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.Continuous;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.RequestHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProcessor {


    private final PersisterService persisterService;
    private final RequestHistoryRepository requestHistoryRepository;

    @Autowired
    public ReportProcessor(PersisterService persisterService, @Qualifier("requestHistoryRepository") RequestHistoryRepository requestHistoryRepository) {
        this.persisterService = persisterService;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public void logRequestHistory(AgentReport agentReport) {
        if (agentReport.getRequestHistoryId() == null) {
            agentReport.setRequestHistoryId(UUID.randomUUID());
        }

        UUID requestHistoryId = agentReport.getRequestHistoryId();

        UUID previousRequestHistoryId = getOptionalPreviousRequestHistoryId(agentReport, requestHistoryId);

    }

    private UUID getOptionalPreviousRequestHistoryId(AgentReport agentReport, UUID requestHistoryId) {
        for (DatamineEntity datamineEntity : agentReport.getEntities()) {
            if (datamineEntity instanceof Continuous) {
//                return requestHistoryRepository.getPreviousRequestHistoryId();
            }
        }
        return null;
    }

}
