package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GroupRepository extends CrudRepository<Group, UUID> {

}
