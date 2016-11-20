package com.github.sinedsem.infgres.repository.datamodel.entities;

import com.github.sinedsem.infgres.repository.datamodel.Node;

import java.util.UUID;

public class DatamineEntity {

    protected UUID id = UUID.randomUUID();
    protected Node node;
    protected long startTime;
    protected long endTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
