package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class PostgresContinuousRepositoryImpl<T extends ContinuousDatamineEntity> extends AbstractRepositoryImpl<T> implements ContinuousRepository<T> {

    public PostgresContinuousRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    @Override
    public T getPrevious(T entity) {

        @SuppressWarnings("JpaQlInspection")
        String sql = "SELECT e FROM " + getDomainClass().getSimpleName() + " e WHERE e.startTime <= :startTime AND " +
                "e.nodeId = :nodeId" + entity.getCriteria() + " ORDER BY e.startTime DESC";

        Query query = entityManager.createQuery(sql);
        query.setParameter("startTime", entity.getStartTime());
        query.setParameter("nodeId", entity.getNodeId());
        entity.setParameters(query);
        query.setMaxResults(1);


        @SuppressWarnings("unchecked")
        List<T> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public T getNext(T entity) {

        @SuppressWarnings("JpaQlInspection")
        String sql = "SELECT e FROM " + getDomainClass().getSimpleName() + " e WHERE e.startTime > :startTime AND " +
                "e.nodeId = :nodeId" + entity.getCriteria() + " ORDER BY e.startTime";

        Query query = entityManager.createQuery(sql);
        query.setParameter("startTime", entity.getStartTime());
        query.setParameter("nodeId", entity.getNodeId());
        entity.setParameters(query);
        query.setMaxResults(1);


        @SuppressWarnings("unchecked")
        List<T> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }
}
