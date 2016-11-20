package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.repository.DatamineDAO;
import com.github.sinedsem.infgres.repository.datamodel.Node;
import com.github.sinedsem.infgres.repository.datamodel.entities.Battery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PostgresDAO implements DatamineDAO {

    protected final JdbcTemplate jdbcTemplate;

    private static final String CREATE_NODE = "INSERT INTO public.node (f_id, f_name) VALUES (?, ?)";
    private static final String CHECK_NODE = "SELECT COUNT(f_id) FROM public.node WHERE f_id=?";

    private static final String BATTERY_DATA_INSERT = "INSERT INTO public.battery (f_id, f_node_id, f_starttime, f_endtime, f_number, f_charge) VALUES (?,?,?,?,?,?)";
    private static final String BATTERY_DATA_FIND_PREV = "SELECT f_id, f_starttime, f_endtime FROM public.battery WHERE f_starttime <= ? AND f_node_id = ?";
    private static final String BATTERY_DATA_FIND_NEXT = "SELECT f_id, f_starttime, f_endtime FROM public.battery WHERE f_starttime > ? AND f_node_id = ?";

    public PostgresDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void createNode(Node node) {
        jdbcTemplate.update(CREATE_NODE, node.getId(), node.getName());
    }

    @Override
    public boolean isNodeExists(Node node) {
        return jdbcTemplate.queryForObject(CHECK_NODE, Integer.class, node.getId()) > 0;
    }

    @Override
    public void insertData(Battery battery) {

        boolean insert = true;
        boolean truncate = false;

        List<Battery> prev = jdbcTemplate.query(BATTERY_DATA_FIND_PREV, new BatteryRowMapper(), battery.getStartTime(), battery.getNode().getId());
        List<Battery> next = jdbcTemplate.query(BATTERY_DATA_FIND_NEXT, new BatteryRowMapper(), battery.getStartTime(), battery.getNode().getId());

        System.out.println(prev.size());
        System.out.println(next.size());


        if (prev.isEmpty() || next.isEmpty()) {
            insert = true;
        }

        if (!prev.isEmpty()){

            if (prev.equals(battery)){
                //extend
            }else{
            // truncate
            // set prev.endtime = battery.starttime-1

            }
        }

        if (insert) {
            jdbcTemplate.update(BATTERY_DATA_INSERT, battery.getId(), battery.getNode().getId(), battery.getStartTime(), battery.getEndTime(), battery.getNumber(), battery.getCharge());
        }
    }


    private static class BatteryRowMapper implements RowMapper<Battery> {

        @Override
        public Battery mapRow(ResultSet rs, int rowNum) throws SQLException {
            Battery battery = new Battery();
            battery.setId(rs.getObject("f_id", UUID.class));
            battery.setStartTime(rs.getLong("f_starttime"));
            battery.setEndTime(rs.getLong("f_endtime"));
            return battery;
        }
    }

}
