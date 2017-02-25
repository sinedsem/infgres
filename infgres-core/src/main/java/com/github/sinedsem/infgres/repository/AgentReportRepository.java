package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AgentReportRepository extends CrudRepository<AgentReport, UUID> {

}
