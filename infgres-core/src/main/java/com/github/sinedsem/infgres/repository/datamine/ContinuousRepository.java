package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface ContinuousRepository<T extends ContinuousDatamineEntity> extends CrudRepository<T, UUID> {

    void getPrevious(T entity);

}
