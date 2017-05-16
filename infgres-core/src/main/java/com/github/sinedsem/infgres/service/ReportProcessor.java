package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Service
public class ReportProcessor {

    private final PostgresPersister postgresPersister;
    private final InfluxPersister influxPersister;
    private final Repositories repositories;
    private final NodeRepository nodeRepository;
    private final RepositoriesService repositoriesService;
    private final ExecutorService nodesExecutor;


    @Autowired
    public ReportProcessor(PostgresPersister postgresPersister, InfluxPersister influxPersister, @Qualifier("repositories") Repositories repositories, NodeRepository nodeRepository, RepositoriesService repositoriesService, ExecutorService nodesExecutor) {
        this.postgresPersister = postgresPersister;
        this.influxPersister = influxPersister;
        this.repositories = repositories;
        this.nodeRepository = nodeRepository;
        this.repositoriesService = repositoriesService;
        this.nodesExecutor = nodesExecutor;
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
        Set<UUID> nodes = new HashSet<>();
//        Utils.seizeEntities(agentReport.getEntities());
        List<EventDatamineEntity> eventEntities = new ArrayList<>(agentReport.getEntities().size());
        for (DatamineEntity datamineEntity : agentReport.getEntities()) {
            nodes.add(datamineEntity.getNodeId());
            datamineEntity.setRequestId(agentReport.getId());
            if (datamineEntity instanceof EventDatamineEntity) {
                eventEntities.add((EventDatamineEntity) datamineEntity);
            } else {
                postgresPersister.persist(datamineEntity);
            }
        }

        nodesExecutor.submit(() -> {
            for (UUID nodeId : nodes) {
                postgresPersister.createNodeIfNotExists(nodeId);
            }
        });

        postgresPersister.persistEventEntities(eventEntities);
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
            repositoriesService.getContinuousRepositoryByClass(BackupConfiguration.class).deleteAll();
            influxPersister.clearDb();
        }
        nodeRepository.deleteAll();
    }
}
