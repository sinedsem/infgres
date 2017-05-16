package com.github.sinedsem.infgres.datamodel.datamine;

import org.influxdb.dto.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Query;
import javax.persistence.Table;

@Entity
@Table(name = "backup_configuration")
public class BackupConfiguration extends ContinuousDatamineEntity {

    @Column(name = "f_level")
    private String level;

    @Column(name = "f_schedule")
    private String schedule;

    @Column(name = "f_path")
    private String path;

    public BackupConfiguration() {
    }

    public BackupConfiguration(BackupConfiguration backupConfiguration) {
        super(backupConfiguration);
        this.level = backupConfiguration.level;
        this.schedule = backupConfiguration.schedule;
        this.path = backupConfiguration.path;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getCriteria() {
        return "";
    }

    @Override
    public void setPostgresParameters(Query query) {
    }

    @Override
    public void setInfluxTagsAndFields(Point.Builder builder) {
        builder.addField("level", level);
        builder.addField("schedule", schedule);
        builder.addField("path", path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackupConfiguration that = (BackupConfiguration) o;

        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = level != null ? level.hashCode() : 0;
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String getTableName() {
        return "backup_configuration";
    }

    @Override
    public String getKey() {
        return super.getKey() + "|" + level;
    }
}
