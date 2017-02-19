package com.github.sinedsem.infgres.config;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.repository.DatamineDAO;
import com.github.sinedsem.infgres.repository.RequestHistoryRepository;
import com.github.sinedsem.infgres.repository.impl.InfluxPostgresDAO;
import com.github.sinedsem.infgres.repository.impl.PostgresDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Config {

    @Value("${storage.influx}")
    private boolean influx;

    @Bean(name = "repository")
    @Autowired
    public DatamineDAO getRepository(JdbcTemplate jdbcTemplate, RequestHistoryRepository requestHistoryRepository) {
        for (AgentReport agentReport : requestHistoryRepository.findAll()) {
            System.out.println(agentReport.getFunction());
        }
        if (influx) {
            return new InfluxPostgresDAO(jdbcTemplate);
        } else {
            return new PostgresDAO(jdbcTemplate);
        }
    }


}
