package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class Reporter {

    private final Repositories repositories;
    private final InfluxReporter influxReporter;


    @Autowired
    public Reporter(@Qualifier("repositories") Repositories repositories, InfluxReporter influxReporter) {
        this.repositories = repositories;
        this.influxReporter = influxReporter;
    }

    public ServerReport makeReport(Collection<UUID> nodeIds, Class<? extends DatamineEntity> clazz, long startTime, long endTime) {

        if (repositories.isInflux()) {
            return influxReporter.makeReport(nodeIds, clazz, startTime, endTime);
        } else {
            throw new UnsupportedOperationException();
        }
    }


}
