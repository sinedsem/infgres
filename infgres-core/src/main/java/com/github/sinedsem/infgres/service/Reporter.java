package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.Grp;
import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.repository.GroupRepository;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class Reporter {

    private final Repositories repositories;
    private final NodeRepository nodeRepository;
    private final InfluxReporter influxReporter;
    private final PostgresReporter postgresReporter;
    private final GroupRepository groupRepository;

    @Autowired
    public Reporter(@Qualifier("repositories") Repositories repositories, InfluxReporter influxReporter, NodeRepository nodeRepository, PostgresReporter postgresReporter, GroupRepository groupRepository) {
        this.repositories = repositories;
        this.influxReporter = influxReporter;
        this.nodeRepository = nodeRepository;
        this.postgresReporter = postgresReporter;
        this.groupRepository = groupRepository;
    }

    public ServerReport makeReport(Collection<UUID> nodeIds, List<UUID> groupIds, long startTime, long endTime) {
        List<Node> byGroup = nodeRepository.findByGroup(groupIds);
        nodeIds.addAll(byGroup.stream().map(Node::getId).collect(Collectors.toSet()));

        ServerReport serverReport = new ServerReport();

        Map<UUID, NodeEntities> map1;
        Map<UUID, NodeEntities> map2;
        if (repositories.isInflux()) {
            map1 = influxReporter.makeReportConfiguration(nodeIds, startTime, endTime);
            map2 = influxReporter.makeReportJob(nodeIds, startTime, endTime);
        } else {
            map1 = postgresReporter.makeReport(nodeIds, BackupConfiguration.class, startTime, endTime);
            map2 = postgresReporter.makeReport(nodeIds, BackupJob.class, startTime, endTime);

        }

        for (Map.Entry<UUID, NodeEntities> entry : map2.entrySet()) {
            if (map1.containsKey(entry.getKey())) {
                map1.get(entry.getKey()).getEntities().addAll(entry.getValue().getEntities());
            } else {
                map1.put(entry.getKey(), entry.getValue());
            }
        }
        serverReport.getNodeEntities().addAll(map1.values());
        return serverReport;
    }


    public List<Node> getNodesList() {
        return Lists.newArrayList(nodeRepository.findAll());
    }

    public List<Grp> getGroupsList() {
        return Lists.newArrayList(groupRepository.findAll());
    }
}
