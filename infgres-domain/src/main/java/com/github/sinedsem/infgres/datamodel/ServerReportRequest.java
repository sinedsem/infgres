package com.github.sinedsem.infgres.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerReportRequest {

    long startTime;
    long endTime;
    List<UUID> nodeIds = new ArrayList<>();
    List<UUID> groupIds = new ArrayList<>();

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

    public List<UUID> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<UUID> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<UUID> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<UUID> groupIds) {
        this.groupIds = groupIds;
    }
}
