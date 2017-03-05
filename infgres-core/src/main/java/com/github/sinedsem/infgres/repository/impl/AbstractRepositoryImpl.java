package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.util.UUID;

public class AbstractRepositoryImpl<T extends DatamineEntity> extends SimpleJpaRepository<T, UUID> {

    final EntityManager entityManager;

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }
}
