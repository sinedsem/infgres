package com.github.sinedsem.infgres.datamodel.datamine;

import org.influxdb.dto.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Query;
import javax.persistence.Table;

@Entity
@Table(name = "disk_status")
public class DiskStatus extends ContinuousDatamineEntity {

    @Column(name = "f_number")
    private int number;

    @Column(name = "f_total_space")
    private long totalSpace;

    @Column(name = "f_used_space")
    private long usedSpace;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    @Override
    public String getCriteria() {
        return " AND number = :number";
    }

    @Override
    public void setPostgresParameters(Query query) {
        query.setParameter("number", number);
    }

    @Override
    public void setInfluxTagsAndFields(Point.Builder builder) {
        builder.tag("number", String.valueOf(number));
        builder.addField("totalSpace", totalSpace);
        builder.addField("usedSpace", usedSpace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiskStatus that = (DiskStatus) o;

        if (number != that.number) return false;
        if (totalSpace != that.totalSpace) return false;
        return usedSpace == that.usedSpace;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + (int) (totalSpace ^ (totalSpace >>> 32));
        result = 31 * result + (int) (usedSpace ^ (usedSpace >>> 32));
        return result;
    }

    @Override
    public String getInfluxMeasurement() {
        return "disk_status";
    }
}
