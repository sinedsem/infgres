package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.config.Repositories;
import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.service.ReportProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/listener")
public class ListenerController {

    private final AtomicLong startTime;
    private final AtomicLong endTime;
    private final AtomicInteger requestCounter;

    private final ReportProcessor reportProcessor;
    private final Repositories repositories;


    @Autowired
    public ListenerController(ReportProcessor reportProcessor, @Qualifier("startTime") AtomicLong startTime, @Qualifier("endTime") AtomicLong endTime, AtomicInteger requestCounter, @Qualifier("repositories") Repositories repositories) {
        this.reportProcessor = reportProcessor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requestCounter = requestCounter;
        this.repositories = repositories;
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    boolean report(@RequestBody AgentReport agentReport) {
        try {
            startTime.compareAndSet(-1, System.nanoTime());
            requestCounter.incrementAndGet();
            reportProcessor.processReport(agentReport);
        } finally {
            requestCounter.decrementAndGet();
        }
        return true;
    }

    @RequestMapping(value = "/resetStopwatch", method = RequestMethod.GET)
    @ResponseBody
    boolean resetTime() {
        startTime.set(-1);
        endTime.set(-1);
        return true;
    }

    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ResponseBody
    long time() {
        if (requestCounter.get() == 0 && startTime.get() != -1 && endTime.get() != -1) {
            return endTime.get() - startTime.get();
        }
        return -1;
    }

    @RequestMapping(value = "/setDb", method = RequestMethod.GET)
    @ResponseBody
    boolean setDb(@RequestParam(defaultValue = "true") Boolean influx) {
        repositories.setInflux(influx);
        return influx;
    }

    @RequestMapping(value = "/clearDbs", method = RequestMethod.GET)
    @ResponseBody
    boolean clearDbs(@RequestParam(defaultValue = "false") Boolean full) {
        reportProcessor.clearDbs(full);
        return true;
    }


}
