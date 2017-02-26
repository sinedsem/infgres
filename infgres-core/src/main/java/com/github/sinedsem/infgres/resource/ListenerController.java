package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.datamodel.AgentReport;
import com.github.sinedsem.infgres.datamodel.datamine.Continuous;
import com.github.sinedsem.infgres.service.ReportProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//todo: all this class

@Controller
@RequestMapping("/listener")
public class ListenerController {

    private final ReportProcessor reportProcessor;

    @Autowired
    public ListenerController(ReportProcessor reportProcessor) {
        this.reportProcessor = reportProcessor;
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    boolean report(@RequestBody AgentReport agentReport) {

        reportProcessor.logRequestHistory(agentReport);
        return agentReport instanceof Continuous;
//        return persisterService.persist(battery);
    }
/*
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    List<KeywordsTemplate> importTemplates(@RequestBody List<KeywordsTemplate> templates) {
        return templatesService.saveTemplates(templates);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    KeywordsTemplate updateTemplate(@RequestBody KeywordsTemplate template) {
        return templatesService.updateTemplate(template);
    }

    @RequestMapping(value = "/{templateId}", method = RequestMethod.DELETE)
    @ResponseBody
    boolean deleteTemplate(@PathVariable UUID templateId) {
        return templatesService.deleteTemplate(templateId);
    }

    @RequestMapping(value = "/move/{destIndex}", method = RequestMethod.POST)
    @ResponseBody
    boolean moveTemplate(@RequestBody UUID templateId, @PathVariable("destIndex") int destIndex) {
        return templatesService.moveTemplate(templateId, destIndex);
    }*/

}
