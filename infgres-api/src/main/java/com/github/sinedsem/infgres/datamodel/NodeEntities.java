package com.github.sinedsem.infgres.datamodel;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodeEntities {

    private UUID nodeId;
    private List<DatamineEntity> entities = new ArrayList<>();

    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    public List<DatamineEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<DatamineEntity> entities) {
        this.entities = entities;
    }
}
