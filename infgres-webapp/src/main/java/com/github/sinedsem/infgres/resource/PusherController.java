package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.service.PusherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pusher")
@CrossOrigin
public class PusherController {


    private final PusherService pusherService;

    @Autowired
    public PusherController(PusherService pusherService) {
        this.pusherService = pusherService;
    }

    @RequestMapping(value = "/push", method = RequestMethod.GET)
    @ResponseBody
    long push() {
        pusherService.pushGeneratedData(100);
        long duration = -1;
        while (duration == -1) {
            duration = pusherService.getDuration();
        }
        return duration / 1_000_000; // return in milliseconds
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    @ResponseBody
    boolean generate() {
        pusherService.generate();
        return true;
    }

    @RequestMapping(value = "/setDb", method = RequestMethod.GET)
    @ResponseBody
    boolean setDb(@RequestParam(defaultValue = "true") Boolean influx) {
        pusherService.setDb(influx);
        return influx;
    }


}
