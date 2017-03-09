package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersisterService {

    private final RepositoriesService repositoriesService;
    private final NodeRepository nodeRepository;

    @Autowired
    public PersisterService(RepositoriesService repositoriesService, NodeRepository nodeRepository) {
        this.repositoriesService = repositoriesService;
        this.nodeRepository = nodeRepository;
    }

    boolean persist(DatamineEntity entity) {
        if (entity instanceof ContinuousDatamineEntity) {
            return persist((ContinuousDatamineEntity) entity);
        } else if (entity instanceof EventDatamineEntity) {
            return persist((EventDatamineEntity) entity);
        }
        return false;
    }

    boolean persist(ContinuousDatamineEntity entity) {
        createNodeIfNotExists(entity);

        ContinuousRepository<ContinuousDatamineEntity> repository = repositoriesService.getRepository(entity);

        ContinuousDatamineEntity previous = repository.getPrevious(entity);

        if (previous != null && previous.equals(entity) && previous.getEndTime() >= entity.getStartTime()) {
            previous.setEndTime(entity.getEndTime());
            previous.setRequestId(entity.getRequestId());
            repository.save(previous);
            entity = previous;
        } else {
            repository.save(entity);
        }

        ContinuousDatamineEntity next = repository.getNext(entity);

        if (next != null && next.equals(entity) && next.getStartTime() <= entity.getEndTime()) {
            next.setRequestId(entity.getRequestId());
            next.setStartTime(entity.getStartTime());
            repository.delete(entity);
            repository.save(next);
        }

        return true;
    }

    boolean persist(EventDatamineEntity entity) {
        createNodeIfNotExists(entity);

        EventRepository<EventDatamineEntity> repository = repositoriesService.getRepository(entity);

        repository.save(entity);
        return true;
    }

    void createNodeIfNotExists(DatamineEntity entity) {
        Node node = nodeRepository.findOne(entity.getNodeId());
        if (node == null) {
            node = new Node();
            node.setId(entity.getNodeId());
            nodeRepository.save(node);
        }
    }

}
