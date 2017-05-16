package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.datamodel.Grp;
import com.github.sinedsem.infgres.datamodel.Node;
import com.github.sinedsem.infgres.datamodel.ServerReport;
import com.github.sinedsem.infgres.datamodel.ServerReportRequest;
import com.github.sinedsem.infgres.service.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/reporter")
public class ReportController {

    private final Reporter reporter;

    @Autowired
    public ReportController(Reporter reporter) {
        this.reporter = reporter;
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    ServerReport report(@RequestBody ServerReportRequest request) {
        return reporter.makeReport(request.getNodeIds(), request.getGroupIds(), request.getStartTime(), request.getEndTime());
    }

    @RequestMapping(value = "/nodes", method = RequestMethod.GET)
    @ResponseBody
    List<Node> nodes() {
        return reporter.getNodesList();
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    List<Grp> groups() {
        return reporter.getGroupsList();
    }


}
