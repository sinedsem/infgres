package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.Grp;
import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.*;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PostgresPersister {

    private final RepositoriesService repositoriesService;
    private final NodeRepository nodeRepository;
    private final AtomicLong endTime;

    @Autowired
    public PostgresPersister(RepositoriesService repositoriesService, NodeRepository nodeRepository, @Qualifier("endTime") AtomicLong endTime) {
        this.repositoriesService = repositoriesService;
        this.nodeRepository = nodeRepository;
        this.endTime = endTime;
    }

    boolean persist(DatamineEntity entity) {
        if (entity instanceof ContinuousDatamineEntity) {
            return persist((ContinuousDatamineEntity) entity);
        } else if (entity instanceof EventDatamineEntity) {
            return persist((EventDatamineEntity) entity);
        }
        return false;
    }

    synchronized boolean persist(ContinuousDatamineEntity entity) {

        @SuppressWarnings("unchecked")
        ContinuousRepository<ContinuousDatamineEntity> repository = repositoriesService.getRepository(entity);

        ContinuousDatamineEntity previous = repository.getPrevious(entity);

        if (previous != null && previous.getEndTime() >= entity.getStartTime()) {
            if (previous.equals(entity)) {
                previous.setEndTime(entity.getEndTime());
                previous.setRequestId(entity.getRequestId());
                repository.save(previous);
                entity = previous;
            } else {
                previous.setEndTime(entity.getStartTime() - 1);
                if (previous.getStartTime() >= previous.getEndTime()) {
                    entity.setId(previous.getId());
                } else {
                    repository.save(previous);
                }
                repository.save(entity);
            }
        } else {
            repository.save(entity);
        }

        ContinuousDatamineEntity next = repository.getNext(entity);

        if (next != null && next.getStartTime() <= entity.getEndTime()) {
            if (next.equals(entity)) {
                next.setRequestId(entity.getRequestId());
                next.setStartTime(entity.getStartTime());
                repository.delete(entity);
                repository.save(next);
            } else {
                entity.setEndTime(next.getStartTime() - 1);
                if (entity.getStartTime() >= entity.getEndTime()) {
                    repository.delete(entity);
                } else {
                    repository.save(entity);
                }
            }
        }

        endTime.set(System.nanoTime());

        return true;
    }

    boolean persist(EventDatamineEntity entity) {
        UUID nodeId = entity.getNodeId();

        EventRepository<EventDatamineEntity> repository = repositoriesService.getRepository(entity);

        repository.save(entity);

        endTime.set(System.nanoTime());
        return true;
    }

    void createNodeIfNotExists(UUID nodeId) {
        Node node = nodeRepository.findOne(nodeId);
        if (node == null) {
            node = new Node();
            node.setId(nodeId);
            node.setName(nodeId.toString());
            node.setGrp(new Grp(UUID.fromString("7502dad7-ea0d-4019-8ac6-71dbdac7d624")));
            nodeRepository.save(node);
        }
    }

    public void persistEntities(List<DatamineEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        List<BackupJob> backupJobs = entities.stream()
                .filter(e -> e instanceof BackupJob)
                .map(e -> (BackupJob)e).collect(Collectors.toList());

        EventRepository backupJobRepository = repositoriesService.getEventRepositoryByClass(BackupJob.class);
        backupJobRepository.save(backupJobs);


        List<BackupConfiguration> backupConfigurations = entities.stream()
                .filter(e -> e instanceof BackupConfiguration)
                .map(e -> (BackupConfiguration)e).collect(Collectors.toList());

        ContinuousRepository backupConfigurationRepository = repositoriesService.getContinuousRepositoryByClass(BackupConfiguration.class);
        backupConfigurationRepository.save(backupConfigurations);

        endTime.set(System.nanoTime());
    }
}
