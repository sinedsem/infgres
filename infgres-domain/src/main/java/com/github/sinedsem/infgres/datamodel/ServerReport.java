package com.github.sinedsem.infgres.datamodel;

import java.util.ArrayList;
import java.util.List;

public class ServerReport {

    private List<NodeEntities> nodeEntities = new ArrayList<>();

    public List<NodeEntities> getNodeEntities() {
        return nodeEntities;
    }

    public void setNodeEntities(List<NodeEntities> nodeEntities) {
        this.nodeEntities = nodeEntities;
    }
}
