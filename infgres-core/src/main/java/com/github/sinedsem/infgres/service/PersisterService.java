package com.github.sinedsem.infgres.service;

import com.github.sinedsem.infgres.repository.DatamineDAO;
import com.github.sinedsem.infgres.repository.datamodel.entities.Battery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PersisterService {


    @Autowired
    @Qualifier("repository")
    DatamineDAO datamineDAO;

    public boolean persist(Battery battery) {
        if (!datamineDAO.isNodeExists(battery.getNode())){
            datamineDAO.createNode(battery.getNode());
        }
        datamineDAO.insertData(battery);
        return true;
    }

}
