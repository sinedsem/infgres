package com.github.sinedsem.infgres.repository.datamine;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public class DatamineRepositoryImpl<T extends DatamineEntity>  implements DatamineRepository<T> {

    private final EntityManager entityManager;

    public DatamineRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void getPrevious(T entity) {
        Query query = entityManager.createQuery("SELECT e from Battery e");
        List resultList = query.getResultList();
        for (Object o : resultList) {
            System.out.println(o);
        }
    }
}
