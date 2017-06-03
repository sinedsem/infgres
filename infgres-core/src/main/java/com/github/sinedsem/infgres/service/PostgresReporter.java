package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.DatamineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.github.sinedsem.infgres.utils.Utils.seizeEntities;

@Service
public class PostgresReporter {

    private final RepositoriesService repositoriesService;

    @Autowired
    public PostgresReporter(RepositoriesService repositoriesService) {
        this.repositoriesService = repositoriesService;
    }

    public Map<UUID, NodeEntities> makeReport(Collection<UUID> nodeIds, Class<? extends DatamineEntity> clazz, long startTime, long endTime) {
        DatamineRepository<DatamineEntity> repository;
        if (ContinuousDatamineEntity.class.isAssignableFrom(clazz)) {
            //noinspection unchecked
            repository = repositoriesService.getContinuousRepositoryByClass((Class<? extends ContinuousDatamineEntity>) clazz);
        } else if (EventDatamineEntity.class.isAssignableFrom(clazz)) {
            //noinspection unchecked
            repository = repositoriesService.getEventRepositoryByClass((Class<? extends EventDatamineEntity>) clazz);
        } else {
            return Collections.emptyMap();
        }

        List<DatamineEntity> entities = repository.makeReport(nodeIds, startTime, endTime);

        if (ContinuousDatamineEntity.class.isAssignableFrom(clazz)) {
            seizeEntities(entities);
        }

        Map<UUID, NodeEntities> nodeEntitiesMap = new HashMap<>();

        for (DatamineEntity entity : entities) {

            NodeEntities nodeEntities;
            if (nodeEntitiesMap.containsKey(entity.getNodeId())) {
                nodeEntities = nodeEntitiesMap.get(entity.getNodeId());
            } else {
                nodeEntities = new NodeEntities();
                nodeEntities.setNodeId(entity.getNodeId());
                nodeEntitiesMap.put(entity.getNodeId(), nodeEntities);
            }
            nodeEntities.getEntities().add(entity);
        }

        return nodeEntitiesMap;

    }

}
