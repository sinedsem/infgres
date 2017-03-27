package com.github.sinedsem.infgres.datamodel.datamine;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "backup_job")
public class BackupJob extends EventDatamineEntity {

    @Column(name = "f_level")
    private String level;

    @Column(name = "f_status")
    private String status;

    @Column(name = "f_error_code")
    private int errorCode;

    @Column(name = "f_job_size")
    private int jobSize;

    @Column(name = "f_path")
    private String path;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getJobSize() {
        return jobSize;
    }

    public void setJobSize(int jobSize) {
        this.jobSize = jobSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getTableName() {
        return "backup_job";
    }
}
