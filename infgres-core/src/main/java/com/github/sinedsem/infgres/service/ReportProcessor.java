package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import com.github.sinedsem.infgres.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProcessor {

    private final PostgresPersister postgresPersister;
    private final InfluxPersister influxPersister;
    private final Repositories repositories;
    private final NodeRepository nodeRepository;
    private final RepositoriesService repositoriesService;


    @Autowired
    public ReportProcessor(PostgresPersister postgresPersister, InfluxPersister influxPersister, @Qualifier("repositories") Repositories repositories, NodeRepository nodeRepository, RepositoriesService repositoriesService) {
        this.postgresPersister = postgresPersister;
        this.influxPersister = influxPersister;
        this.repositories = repositories;
        this.nodeRepository = nodeRepository;
        this.repositoriesService = repositoriesService;
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
//        Utils.seizeEntities(agentReport.getEntities());
        for (DatamineEntity datamineEntity : agentReport.getEntities()) {
//            datamineEntity.setEndTime(agentReport.getEndTime());
//            datamineEntity.setStartTime(agentReport.getStartTime());
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

    public void clearDbs(Boolean full) {
        if (full) {
            repositoriesService.getEventRepositoryByClass(BackupJob.class).deleteAll();
            repositoriesService.getContinuousRepositoryByClass(DiskStatus.class).deleteAll();
            influxPersister.clearDb();
        }
        nodeRepository.deleteAll();
    }
}
