package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;

import java.util.List;

public interface DiskStatusRepository extends ContinuousRepository<DiskStatus>, DatamineRepository<DiskStatus> {

    List<DiskStatus> findByOrderByStartTime();
}
