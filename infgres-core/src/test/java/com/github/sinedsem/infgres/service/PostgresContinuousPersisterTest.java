package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import com.github.sinedsem.infgres.repository.datamine.DiskStatusRepository;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class PostgresContinuousPersisterTest {

    @Autowired
    private RepositoriesService repositoriesService;

    @Autowired
    private PostgresPersister postgresPersister;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
    }

    @After
    public void clear() {
        jdbcTemplate.execute("DELETE FROM public.disk_status");
    }

    @Test
    public void testExtend() {

        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");
        UUID firstId = entity.getId();

        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(20);
        entity.setEndTime(40);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(40, list.get(0).getEndTime());
        assertEquals(firstId, list.get(0).getId());

    }

    @Test
    public void testExtendBack() {
        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");
        UUID firstId = entity.getId();

        entity.setStartTime(20);
        entity.setEndTime(40);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(10);
        entity.setEndTime(30);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(40, list.get(0).getEndTime());
        assertEquals(firstId, list.get(0).getId());
    }

    @Test
    public void testCut() {
        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");
        UUID firstId = entity.getId();

        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(20);
        entity.setEndTime(40);
        entity.setUsedSpace(60);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(2, list.size());
        assertEquals(entity, list.get(1));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(19, list.get(0).getEndTime());
        assertEquals(20, list.get(1).getStartTime());
        assertEquals(40, list.get(1).getEndTime());
        assertEquals(firstId, list.get(0).getId());
        assertEquals(entity.getId(), list.get(1).getId());
    }

    @Test
    public void testCutInserted() {
        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");
        UUID firstId = entity.getId();

        entity.setStartTime(20);
        entity.setEndTime(40);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setUsedSpace(60);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(2, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(19, list.get(0).getEndTime());
        assertEquals(20, list.get(1).getStartTime());
        assertEquals(40, list.get(1).getEndTime());
        assertEquals(firstId, list.get(1).getId());
        assertEquals(entity.getId(), list.get(0).getId());
    }

    @Test
    public void testReplace() {
        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");
        UUID firstId = entity.getId();

        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(10);
        entity.setEndTime(20);
        entity.setUsedSpace(60);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(20, list.get(0).getEndTime());
        assertEquals(firstId, list.get(0).getId());
    }

    @Test
    public void testGlue() {
        DiskStatus entity = new DiskStatus();

        UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");

        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setNumber(1);
        entity.setTotalSpace(100);
        entity.setUsedSpace(50);
        entity.setNodeId(nodeId);
        postgresPersister.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(40);
        entity.setEndTime(60);
        postgresPersister.persist(entity);

        UUID secondId = entity.getId();

        entity.setId(UUID.randomUUID());
        entity.setStartTime(20);
        entity.setEndTime(40);
        postgresPersister.persist(entity);

        DiskStatusRepository repository = (DiskStatusRepository) repositoriesService.getRepository(entity);
        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(60, list.get(0).getEndTime());
        assertEquals(secondId, list.get(0).getId());
    }

}