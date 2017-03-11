package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface DatamineRepository<T extends DatamineEntity> {

    List<T> findByOrderByStartTime();
}
