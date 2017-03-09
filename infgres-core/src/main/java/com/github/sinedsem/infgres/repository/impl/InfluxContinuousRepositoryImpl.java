package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;

import javax.persistence.EntityManager;

public class InfluxContinuousRepositoryImpl<T extends ContinuousDatamineEntity> extends AbstractRepositoryImpl<T> implements ContinuousRepository<T> {

    public InfluxContinuousRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    @Override
    public T getPrevious(T entity) {
        return null;
    }

    @Override
    public T getNext(T entity) {
        return null;
    }
}
