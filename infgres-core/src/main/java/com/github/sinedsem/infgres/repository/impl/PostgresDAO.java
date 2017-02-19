package com.github.sinedsem.infgres.repository.impl;

import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.datamine.Battery;
import com.github.sinedsem.infgres.repository.DatamineDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PostgresDAO implements DatamineDAO {




    protected final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private BatteryRepository batteryRepository;

    public PostgresDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    @PostConstruct
//    private void init() {
//        for (Battery battery : batteryRepository.findAll()) {
//            System.out.println(battery.getId());
//        }
//    }


    @Override
    public void createNode(Node node) {
        jdbcTemplate.update("INSERT INTO public.node (f_id, f_name) VALUES (?, ?)", node.getId(), node.getName());
    }

    @Override
    public boolean isNodeExists(Node node) {
        return jdbcTemplate.queryForObject("SELECT COUNT(f_id) FROM public.node WHERE f_id=?", Integer.class, node.getId()) > 0;
    }

    @Override
    public void insertData(Battery battery) {

        boolean insert = true;
        boolean truncate = false;

        List<Battery> prev = jdbcTemplate.query("SELECT f_id, f_starttime, f_endtime FROM public.battery WHERE f_starttime <= ? AND f_node_id = ?", new BatteryRowMapper(), battery.getStartTime(), battery.getNodeId());
        List<Battery> next = jdbcTemplate.query("SELECT f_id, f_starttime, f_endtime FROM public.battery WHERE f_starttime > ? AND f_node_id = ?", new BatteryRowMapper(), battery.getStartTime(), battery.getNodeId());

        System.out.println(prev.size());
        System.out.println(next.size());


        if (prev.isEmpty() || next.isEmpty()) {
            insert = true;
        }

        if (!prev.isEmpty()) {

            if (prev.equals(battery)) {
                //extend
            } else {
                // truncate
                // set prev.endtime = battery.starttime-1

            }
        }

        if (insert) {
            jdbcTemplate.update("INSERT INTO public.battery (f_id, f_node_id, f_starttime, f_endtime, f_number, f_charge) VALUES (?,?,?,?,?,?)",
                    battery.getId(), battery.getNodeId(), battery.getStartTime(), battery.getEndTime(), battery.getNumber(), battery.getCharge());
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
