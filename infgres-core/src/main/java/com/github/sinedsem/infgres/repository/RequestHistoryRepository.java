package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestHistoryRepository extends CrudRepository<AgentReport, UUID> {

    @Query(value = "select f_id from request_history e where e.f_starttime = " +
            "(select max(e1.f_starttime) from request_history e1 " +
            "where e1.f_module = ? and e1.f_function = ? and e1.f_id <> ? and e1.f_target_id = ? and e1.f_endtime IS NOT NULL and e1.f_starttime < ?) " +
            "and " +
            "e.f_module = ? and e.f_function = ? AND e.f_target_id = ? and " +
            "e.f_id <> ? and " +
            "e.f_endtime IS NOT NULL order by f_last_modified_long desc"
            , nativeQuery = true)
    UUID findByFunctionAndModule();
}
