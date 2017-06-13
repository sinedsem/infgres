package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.NodeEntities;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.ServerReportRequest;
import com.github.sinedsem.infgres.datamodel.datamine.BackupConfiguration;
import com.github.sinedsem.infgres.repository.datamine.BackupConfigurationRepository;
import com.github.sinedsem.infgres.service.RepositoriesService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class IntegrationTest {

    private UUID nodeId = UUID.fromString("b81af4fa-7946-4cbe-b615-c98ac825445e");

    @Autowired
    private RepositoriesService repositoriesService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ListenerController listenerController;

    @Autowired
    private ReportController reportController;

    @Before
    public void init() {
    }

    @After
    public void clear() {
        jdbcTemplate.execute("DELETE FROM public.backup_configuration");
    }

    @Test
    public void testWritePostgres() throws Exception {
        write(false);

        while (listenerController.time() == -1) {
            Thread.sleep(1000);
        }

        BackupConfigurationRepository repository = (BackupConfigurationRepository) repositoriesService.getRepository(new BackupConfiguration());
        ArrayList<BackupConfiguration> list = Lists.newArrayList(repository.findByOrderByStartTime());

        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getStartTime());
        assertEquals(30, list.get(0).getEndTime());
    }


    public void write(boolean influx) throws Exception {
        listenerController.setDb(influx);

        BackupConfiguration entity = new BackupConfiguration();


        entity.setStartTime(10);
        entity.setEndTime(30);
        entity.setLevel("Full");
        entity.setSchedule("cron");
        entity.setPath("Path one");
        entity.setNodeId(nodeId);


        AgentReport agentReport = new AgentReport();
        agentReport.getEntities().add(entity);

        listenerController.report(agentReport);

    }


    @Test
    public void testReadInflux() throws Exception {
        write(true);


        while (listenerController.time() == -1) {
            Thread.sleep(1000);
        }

        testRead();
    }

    @Test
    public void testReadPostgres() throws Exception {
        write(false);


        while (listenerController.time() == -1) {
            Thread.sleep(1000);
        }

        testRead();
    }


    public void testRead() throws Exception {

        ServerReportRequest request = new ServerReportRequest();
        request.setStartTime(0);
        request.setEndTime(40);
        request.setNodeIds(Collections.singletonList(nodeId));
        ServerReport report = reportController.report(request);
        List<NodeEntities> list = report.getNodeEntities();


        assertEquals(1, list.size());
        BackupConfiguration datamineEntity = (BackupConfiguration) list.get(0).getEntities().get(0);
        assertEquals(10, datamineEntity.getStartTime());
        assertEquals(30, datamineEntity.getEndTime());
        assertEquals("Path one", datamineEntity.getPath());
        assertEquals("cron", datamineEntity.getSchedule());
        assertEquals("Full", datamineEntity.getLevel());

    }

}