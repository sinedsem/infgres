package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.github.sinedsem.infgres.repository.datamine.DatamineCrudRepository;
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
        Node node = nodeRepository.findOne(entity.getNodeId());
        if (node == null) {
            node = new Node();
            node.setId(entity.getNodeId());
            nodeRepository.save(node);
        }
        DatamineCrudRepository<DatamineEntity> repository = repositoriesService.getRepository(entity);
        repository.getPrevious(entity);
        repository.save(entity);
        return true;
    }

}
