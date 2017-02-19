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
    private UUID requestHistoryId;

    @Transient
    private List<DatamineEntity> entities = new ArrayList<>();

    @Column(name = "f_function")
    private String function;

    @Column(name = "f_module")
    private String module;

    @Column(name = "f_starttime")
    private long startTime;

    @Column(name = "f_endtime")
    private long endTime;

    public UUID getRequestHistoryId() {
        return requestHistoryId;
    }

    public void setRequestHistoryId(UUID requestHistoryId) {
        this.requestHistoryId = requestHistoryId;
    }

    public List<DatamineEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<DatamineEntity> entities) {
        this.entities = entities;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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
