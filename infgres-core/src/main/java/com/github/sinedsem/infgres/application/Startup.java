package com.github.sinedsem.infgres.application;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component("startup")
public class Startup {

//    private final SetupInfoDAO setupInfoDAO;
//
//    private final TemplatesDAO templatesDAO;

//    @Autowired
//    public Startup(TemplatesDAO templatesDAO, SetupInfoDAO setupInfoDAO) {
//        this.templatesDAO = templatesDAO;
//        this.setupInfoDAO = setupInfoDAO;
//    }

    @PostConstruct
    public void init() throws IOException {
//        if (!setupInfoDAO.areTablesExist()) {
//            setupInfoDAO.createTables();
//        }

    }
}

