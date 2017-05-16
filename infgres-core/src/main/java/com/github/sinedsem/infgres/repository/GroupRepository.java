package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.Grp;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GroupRepository extends CrudRepository<Grp, UUID> {

}
