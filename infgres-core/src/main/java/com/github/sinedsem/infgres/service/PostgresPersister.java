package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.Grp;
import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.datamodel.datamine.BackupJob;
import com.github.sinedsem.infgres.datamodel.datamine.DatamineEntity;
import com.github.sinedsem.infgres.repository.NodeRepository;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class PostgresPersister {

    private final RepositoriesService repositoriesService;

    private final ExecutorService persisterExecutor;

    private volatile Set<DatamineEntity> entitiesToPersist = ConcurrentHashMap.newKeySet();
    private Lock lock = new ReentrantLock();

    private final AtomicLong endTime;

    private final NodeRepository nodeRepository;

    @Autowired
    public PostgresPersister(RepositoriesService repositoriesService, @Qualifier("endTime") AtomicLong endTime, NodeRepository nodeRepository) {
        this.repositoriesService = repositoriesService;
        this.endTime = endTime;
        this.persisterExecutor = Executors.newSingleThreadExecutor();
        this.nodeRepository = nodeRepository;
    }

    boolean persist(AgentReport agentReport) {

        entitiesToPersist.addAll(agentReport.getEntities());
        endTime.set(-1);

        // executing in another thread to release http request
        persisterExecutor.submit(() -> {
            if (lock.tryLock()) {
                try {
                    doPersist();
                } finally {
                    lock.unlock();
                }
            }
        });

        return true;
    }

    private void doPersist() {
        if (entitiesToPersist == null || entitiesToPersist.isEmpty()) {
            return;
        }

        List<BackupJob> backupJobs = entitiesToPersist.stream()
                .filter(e -> e instanceof BackupJob)
                .map(e -> (BackupJob)e).collect(Collectors.toList());

        EventRepository backupJobRepository = repositoriesService.getEventRepositoryByClass(BackupJob.class);
        backupJobRepository.save(backupJobs);


        List<BackupConfiguration> backupConfigurations = entitiesToPersist.stream()
                .filter(e -> e instanceof BackupConfiguration)
                .map(e -> (BackupConfiguration)e).collect(Collectors.toList());

        ContinuousRepository backupConfigurationRepository = repositoriesService.getContinuousRepositoryByClass(BackupConfiguration.class);
        backupConfigurationRepository.save(backupConfigurations);

        endTime.set(System.nanoTime());

    }

    void createNodeIfNotExists(UUID nodeId) {
        Node node = nodeRepository.findOne(nodeId);
        if (node == null) {
            node = new Node();
            node.setId(nodeId);
            node.setName(nodeId.toString());
            node.setGrp(new Grp(UUID.fromString("7502dad7-ea0d-4019-8ac6-71dbdac7d624")));
            nodeRepository.save(node);
        }
    }

}
