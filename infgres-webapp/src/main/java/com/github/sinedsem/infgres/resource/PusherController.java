package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.service.PusherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/pusher")
@CrossOrigin
public class PusherController {


    private final PusherService pusherService;

    @Autowired
    public PusherController(PusherService pusherService) {
        this.pusherService = pusherService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    boolean report() {
        pusherService.push();
        return true;
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    @ResponseBody
    boolean generate() {
        return true;
    }

}
