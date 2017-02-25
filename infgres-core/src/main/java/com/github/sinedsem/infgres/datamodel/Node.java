package com.github.sinedsem.infgres.datamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Node {

    @Id
    @Column(name = "f_id")
    private UUID id;

    @Column(name = "f_name")
    private String name;

    public Node() {
    }

    public Node(UUID id) {
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
