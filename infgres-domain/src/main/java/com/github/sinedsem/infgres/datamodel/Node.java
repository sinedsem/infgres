package com.github.sinedsem.infgres.datamodel;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "node")
public class Node {

    @Id
    @Column(name = "f_id")
    private UUID id;

    @Column(name = "f_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "f_grp_id")
    private Group group;

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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
