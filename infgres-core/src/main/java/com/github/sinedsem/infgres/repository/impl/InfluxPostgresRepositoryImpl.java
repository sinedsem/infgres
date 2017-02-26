package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;

import javax.persistence.EntityManager;

public class InfluxPostgresRepositoryImpl<T extends DatamineEntity> extends PostgresRepositoryImpl<T> {

    public InfluxPostgresRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    @Override
    public <S extends T> S save(S entity) {
        return super.save(entity);
    }

    @Override
    public void getPrevious(T entity) {
        super.getPrevious(entity);
    }
}
