package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.datamodel.datamine.DiskStatus;
import com.github.sinedsem.infgres.repository.datamine.ContinuousRepository;
import com.github.sinedsem.infgres.repository.datamine.DiskStatusRepository;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersisterServiceTest {

    @Autowired
    private RepositoriesService repositoriesService;

    @Autowired
    private PersisterService persisterService;

    @Autowired
    private EntityManager em;

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
        persisterService.persist(entity);

        entity.setId(UUID.randomUUID());
        entity.setStartTime(20);
        entity.setEndTime(40);
        persisterService.persist(entity);

        ContinuousRepository<DiskStatus> repository = repositoriesService.getRepository(entity);

        ArrayList<DiskStatus> list = Lists.newArrayList(repository.findAll());

        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(40, list.get(0).getEndTime());
        assertEquals(firstId, list.get(0).getId());

    }

}