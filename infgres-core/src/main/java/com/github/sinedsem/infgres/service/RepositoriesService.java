package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.DatamineCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class RepositoriesService {

    private Repositories repositories = null;

    @Autowired
    public RepositoriesService(WebApplicationContext appContext) {
        repositories = new Repositories(appContext);
    }

    <T extends DatamineEntity> DatamineCrudRepository<T> getRepository(T entity) {
        //noinspection unchecked
        return (DatamineCrudRepository<T>) repositories.getRepositoryFor(entity.getClass());
    }
}