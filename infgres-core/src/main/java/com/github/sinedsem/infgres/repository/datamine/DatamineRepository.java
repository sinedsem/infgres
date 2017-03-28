package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface DatamineRepository<T extends DatamineEntity> {

    List<T> makeReport(Collection<UUID> nodeIds, long startTime, long endTime);
}
