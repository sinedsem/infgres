package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.DatamineRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AbstractRepositoryImpl<T extends DatamineEntity> extends SimpleJpaRepository<T, UUID> implements DatamineRepository<T> {

    final EntityManager entityManager;

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<T> makeReport(Collection<UUID> nodeIds, long startTime, long endTime) {

        @SuppressWarnings("JpaQlInspection")
        String sql = "SELECT e FROM " + getDomainClass().getSimpleName() + " e WHERE (e.startTime >= :startTime AND " +
                "e.startTime <= :endTime OR e.startTime < :startTime AND e.endTime >= :startTime) AND e.nodeId IN :nodeIds ORDER BY e.startTime";

        Query query = entityManager.createQuery(sql);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        query.setParameter("nodeIds", nodeIds);

        @SuppressWarnings("unchecked")
        List<T> resultList = query.getResultList();
        return resultList;
    }
}
