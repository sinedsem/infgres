package com.github.sinedsem.infgres.datamodel.datamine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "datamineType")
@JsonSubTypes({@JsonSubTypes.Type(value = Battery.class)})
public abstract class DatamineEntity {

    private UUID id = UUID.randomUUID();
    private UUID nodeId;
    private UUID requestHistoryId;
    private long startTime;
    private long endTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    public UUID getRequestHistoryId() {
        return requestHistoryId;
    }

    public void setRequestHistoryId(UUID requestHistoryId) {
        this.requestHistoryId = requestHistoryId;
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
