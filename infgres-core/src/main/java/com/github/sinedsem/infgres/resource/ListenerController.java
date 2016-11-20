package com.github.sinedsem.infgres.resource;

import com.github.sinedsem.infgres.repository.datamodel.entities.Battery;
import com.github.sinedsem.infgres.service.PersisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/listener")
public class ListenerController {

    private final PersisterService persisterService;

    public ListenerController(PersisterService persisterService) {
        this.persisterService = persisterService;
    }

    @RequestMapping(value = "/battery", method = RequestMethod.POST)
    @ResponseBody
    boolean persistBattery(@RequestBody Battery battery) {
        return persisterService.persist(battery);
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
