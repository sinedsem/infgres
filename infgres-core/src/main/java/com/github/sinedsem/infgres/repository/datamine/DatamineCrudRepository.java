package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface DatamineCrudRepository<T extends DatamineEntity> extends CrudRepository<T, UUID> {

    void getPrevious(T entity);

}
