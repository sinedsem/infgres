package com.github.sinedsem.infgres.datamodel.datamine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "datamineType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DiskStatus.class),
        @JsonSubTypes.Type(value = BackupJob.class)
})
@MappedSuperclass
public abstract class DatamineEntity {

    @Id
    @Column(name = "f_id")
    private UUID id = UUID.randomUUID();

    @Column(name = "f_node_id")
    private UUID nodeId;

    @Column(name = "f_request_id")
    private UUID requestId;

    @Column(name = "f_starttime")
    private long startTime;

    @Column(name = "f_endtime")
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

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
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
