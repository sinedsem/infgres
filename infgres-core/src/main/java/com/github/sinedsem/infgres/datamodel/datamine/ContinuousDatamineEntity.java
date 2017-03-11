package com.github.sinedsem.infgres.datamodel.datamine;

import org.influxdb.dto.Point;

import javax.persistence.Query;

public abstract class ContinuousDatamineEntity extends DatamineEntity {

    public String getCriteria() {
        return "";
    }

    public void setPostgresParameters(Query query) {
    }

    public void setInfluxTagsAndFields(Point.Builder builder) {
    }

}
