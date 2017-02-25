package com.github.sinedsem.infgres.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${storage.influx}")
    private boolean influx;

//    @Bean(name = "repository")
//    @Autowired
//    public DatamineDAO getRepository(JdbcTemplate jdbcTemplate, AgentReportRepository agentReportRepository) {
//        for (AgentReport agentReport : agentReportRepository.findAll()) {
//            System.out.println(agentReport.getFunction());
//        }
//        if (influx) {
//            return new InfluxPostgresDAO(jdbcTemplate);
//        } else {
//            return new PostgresDAO(jdbcTemplate);
//        }
//    }


}
