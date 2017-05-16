package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;

public interface DatamineDAO {

    void createNode(Node node);

    boolean isNodeExists(Node node);

    void insertData(BackupConfiguration backupConfiguration);
}
