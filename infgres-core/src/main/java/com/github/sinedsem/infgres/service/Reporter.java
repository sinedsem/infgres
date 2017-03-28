package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class Reporter {

    private final Repositories repositories;
    private final NodeRepository nodeRepository;
    private final InfluxReporter influxReporter;
    private final PostgresReporter postgresReporter;


    @Autowired
    public Reporter(@Qualifier("repositories") Repositories repositories, InfluxReporter influxReporter, NodeRepository nodeRepository, PostgresReporter postgresReporter) {
        this.repositories = repositories;
        this.influxReporter = influxReporter;
        this.nodeRepository = nodeRepository;
        this.postgresReporter = postgresReporter;
    }

    public ServerReport makeReport(Collection<UUID> nodeIds, Class<? extends DatamineEntity> clazz, long startTime, long endTime) {

        if (repositories.isInflux()) {
            return influxReporter.makeReport(nodeIds, clazz, startTime, endTime);
        } else {
            return postgresReporter.makeReport(nodeIds, clazz, startTime, endTime);
        }
    }


    public List<Node> getNodesList() {
        return Lists.newArrayList(nodeRepository.findAll());
    }
}
