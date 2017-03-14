package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.service.PusherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.ResultSet;
import java.util.UUID;

@Controller
@RequestMapping("/pusher")
public class ListenerController {


    private final PusherService pusherService;

    @Autowired
    public ListenerController(PusherService pusherService) {
        this.pusherService = pusherService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    boolean report() {
        pusherService.push();
        return true;
    }

}
