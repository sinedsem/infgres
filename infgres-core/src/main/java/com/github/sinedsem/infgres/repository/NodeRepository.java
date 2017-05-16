package com.github.sinedsem.infgres.repository;

import com.github.sinedsem.infgres.datamodel.Node;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends CrudRepository<Node, UUID> {

    @Query("select n from Node n where n.grp.id in :ids")
    List<Node> findByGroup(@Param("ids") List<UUID> groupIds);
}
