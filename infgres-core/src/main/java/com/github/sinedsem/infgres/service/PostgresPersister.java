package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

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

    @Transactional
    boolean persist(ContinuousDatamineEntity entity) {
        createNodeIfNotExists(entity.getNodeId());

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
        createNodeIfNotExists(entity.getNodeId());

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
            nodeRepository.save(node);
        }
    }

}
