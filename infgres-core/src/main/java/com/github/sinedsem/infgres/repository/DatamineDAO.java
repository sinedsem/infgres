package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.repository.datamodel.Node;
import com.github.sinedsem.infgres.repository.datamodel.entities.Battery;

public interface DatamineDAO {

    void createNode(Node node);

    boolean isNodeExists(Node node);

    void insertData(Battery battery);
}
