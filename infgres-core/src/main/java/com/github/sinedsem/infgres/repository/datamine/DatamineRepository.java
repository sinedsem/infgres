package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;

public interface DatamineRepository<T extends DatamineEntity> {
    void getPrevious(T entity);
}
