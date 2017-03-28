package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface EventRepository<T extends EventDatamineEntity> extends CrudRepository<T, UUID>, DatamineRepository<T> {


}
