package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.datamine.ContinuousDatamineEntity;
import com.github.sinedsem.infgres.datamodel.datamine.EventDatamineEntity;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;
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

    ContinuousRepository getRepository(ContinuousDatamineEntity entity) {
        //noinspection unchecked
        return (ContinuousRepository) repositories.getRepositoryFor(entity.getClass());
    }

    <T extends EventDatamineEntity> EventRepository<T> getRepository(T entity) {
        //noinspection unchecked
        return (EventRepository<T>) repositories.getRepositoryFor(entity.getClass());
    }
}