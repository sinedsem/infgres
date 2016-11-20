package com.github.sinedsem.infgres.repository.datamodel;

import java.util.UUID;

public class Node {

    private UUID id;
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
