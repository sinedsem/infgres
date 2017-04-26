package com.github.sinedsem.infgres.datamodel;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "request_history")
public class AgentReport {

    @Column(name = "f_id")
    @Id
    private UUID id;

    @Transient
    private List<DatamineEntity> entities = new ArrayList<>();

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

    public List<DatamineEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<DatamineEntity> entities) {
        this.entities = entities;
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
