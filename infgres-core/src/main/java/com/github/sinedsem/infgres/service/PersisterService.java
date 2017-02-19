package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.repository.DatamineDAO;
import com.github.sinedsem.infgres.datamodel.datamine.Battery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PersisterService {


    private final DatamineDAO datamineDAO;

    @Autowired
    public PersisterService(@Qualifier("repository") DatamineDAO datamineDAO) {
        this.datamineDAO = datamineDAO;
    }

    public boolean persist(Battery battery) {
//        if (!datamineDAO.isNodeExists(battery.getNodeId())) {
//            datamineDAO.createNode(battery.getNodeId());
//        }
        datamineDAO.insertData(battery);
        return true;
    }

}
