package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;

import java.util.List;

public interface BackupConfigurationRepository extends ContinuousRepository<BackupConfiguration>, DatamineRepository<BackupConfiguration> {

    List<BackupConfiguration> findByOrderByStartTime();
}
