package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.Node;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NodeRepository extends CrudRepository<Node, UUID> {

}
