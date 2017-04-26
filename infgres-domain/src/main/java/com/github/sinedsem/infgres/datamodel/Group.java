package com.github.sinedsem.infgres.datamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "grp")
public class Group {

    @Id
    @Column(name = "f_id")
    private UUID id;

    @Column(name = "f_name")
    private String name;

    public Group() {
    }

    public Group(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
