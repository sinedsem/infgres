package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.DatamineCrudRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public class PostgresRepositoryImpl<T extends DatamineEntity> extends SimpleJpaRepository<T, UUID> implements DatamineCrudRepository<T> {

    private final EntityManager entityManager;

    public PostgresRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public void getPrevious(T entity) {
        Query query = entityManager.createQuery("SELECT e from Battery e");
        List resultList = query.getResultList();
        for (Object o : resultList) {
            System.out.println(o);
        }
    }
}
