package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;

import javax.persistence.EntityManager;

public class PostgresEventRepositoryImpl<T extends EventDatamineEntity> extends AbstractRepositoryImpl<T> implements EventRepository<T> {

    public PostgresEventRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

}
