package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.datamodel.ServerReportRequest;
import com.github.sinedsem.infgres.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
@CrossOrigin
public class WebController {


    private final WebService webService;

    @Autowired
    public WebController(WebService webService) {
        this.webService = webService;
    }

    @RequestMapping(value = "/push", method = RequestMethod.GET)
    @ResponseBody
    long push() {
        webService.pushGeneratedData(100);
        long duration = -1;
        while (duration == -1) {
            duration = webService.getDuration();
        }
        return duration / 1_000_000; // return in milliseconds
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    @ResponseBody
    boolean generate() {
        webService.generate();
        return true;
    }

    @RequestMapping(value = "/setDb", method = RequestMethod.GET)
    @ResponseBody
    boolean setDb(@RequestParam(defaultValue = "true") Boolean influx) {
        webService.setDb(influx);
        return influx;
    }

    @RequestMapping(value = "/clearDbs", method = RequestMethod.GET)
    @ResponseBody
    boolean clearDbs(@RequestParam(defaultValue = "false") Boolean full) {
        return webService.clearDbs(full);
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public byte[] report(@RequestBody ServerReportRequest request) {
        return webService.getReport(request);
    }

    @RequestMapping(value = "/nodes", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public byte[] loadNodes() {
        return webService.loadNodes();
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public byte[] loadGroups() {
        return webService.loadGroups();
    }

    @RequestMapping(value = "/reportDuration", method = RequestMethod.GET)
    @ResponseBody
    public long getReportDuration() {
        return webService.getReportDuration() / 1_000_000; // return in milliseconds
    }

}
