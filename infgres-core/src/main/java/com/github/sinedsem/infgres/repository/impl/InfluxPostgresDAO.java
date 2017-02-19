package com.github.sinedsem.infgres.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;

//@Repository
public class InfluxPostgresDAO extends PostgresDAO {

    public InfluxPostgresDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

}
