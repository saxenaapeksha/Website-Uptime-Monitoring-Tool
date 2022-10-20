package com.example.wup.controller;

import com.example.wup.exception.FrequencyOutOfRangeException;
import com.example.wup.model.CheckScheduler;
import com.example.wup.model.Website;
import com.example.wup.repository.CheckSchedulerRepository;
import com.example.wup.repository.WebsiteRepository;
import com.example.wup.pojo.Frequency;
import com.example.wup.utility.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/check")
public class CheckSchedulerController {

    //This is configurable and will be set by the user later
    private final Long MAX_FAILED_ATTEMPT = 3L;
    private final String EMAIL = "net.apeksha@gmail.com";
    private final CheckSchedulerRepository checkRepository;
    private final WebsiteRepository websiteRepository;

    public CheckSchedulerController(CheckSchedulerRepository checkRepository, WebsiteRepository websiteRepository) {
        this.checkRepository = checkRepository;
        this.websiteRepository = websiteRepository;
    }

    private static Website populateWebsiteObject(CheckScheduler checkObject) {
        Website website = new Website();
        website.setNextRun(new Date());
        website.setCheckScheduler(checkObject);
        website.setLastRun(null);
        website.setLastStatusChange(null);
        website.setTotalResponseTime(null);
        website.setHits(null);
        website.setFailedAttempts(0L);
        return website;
    }

    private static com.example.wup.pojo.CheckScheduler setCheckScheduler(Website websiteObject) {
        com.example.wup.pojo.CheckScheduler checkSchedulerObject = new com.example.wup.pojo.CheckScheduler();
        CheckScheduler checkSchedulerModel = websiteObject.getCheckScheduler();
        checkSchedulerObject.setId(checkSchedulerModel.getId());
        checkSchedulerObject.setWebsiteURL(checkSchedulerModel.getWebsiteURL());
        checkSchedulerObject.setName(checkSchedulerModel.getName());
        checkSchedulerObject.setFrequency(checkSchedulerModel.getFrequency());
        checkSchedulerObject.setFrequencyUnit(checkSchedulerModel.getFrequencyUnit());
        return checkSchedulerObject;
    }

    @RequestMapping("/all")
    public ResponseEntity findAll() {
        return ResponseEntity.ok(checkRepository.findAll());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody CheckScheduler checkObject) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "register new check failed");
        body.put("error", "true");
        checkObject.setActive(Boolean.TRUE);
        checkObject.setMaxFailedAttempt(MAX_FAILED_ATTEMPT);
        checkObject.setEmail(EMAIL);
        CheckScheduler checkRegistry = checkRepository.save(checkObject);
        if (checkRegistry == null)
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        Website website = populateWebsiteObject(checkRegistry);
        if (website == null)
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        Website websiteObject = websiteRepository.save(website);
        com.example.wup.pojo.CheckScheduler checkSchedulerObject = setCheckScheduler(websiteObject);
        return ResponseEntity.ok(checkSchedulerObject);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity filter(@RequestParam(required = false) String frequency, @RequestParam(required = false) String name) throws FrequencyOutOfRangeException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Both frequency and name filters are null, either one of the request param is mandatory.");
        body.put("error", "true");
        List<CheckScheduler> checkScheduler = null;
        if (frequency != null) {
            Frequency frequencyObj = Utility.validateFrequency(frequency);
            checkScheduler = checkRepository.filterByFrequency(frequencyObj.getNumber(), frequencyObj.getUnit());
            return ResponseEntity.ok(checkScheduler);
        } else if (name != null) {
            checkScheduler = checkRepository.filterByName(name);
            return ResponseEntity.ok(checkScheduler);
        }
        return new ResponseEntity(body, HttpStatus.valueOf(200));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity activate(@RequestParam int id) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Check Activated");
        body.put("success", "true");
        boolean isUpdated = checkRepository.updateStatus(true, id) > 0;
        if (isUpdated)
            return new ResponseEntity(body,HttpStatus.valueOf(200));
        body.put("message", "Check activation failed");
        body.put("error", "true");
        return new ResponseEntity(body,HttpStatus.valueOf(200));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deactivate(@RequestParam int id) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Check Deactivated");
        body.put("success", "true");
        boolean isUpdated = checkRepository.updateStatus(true, id) > 0;
        if (isUpdated)
            return new ResponseEntity(body,HttpStatus.valueOf(200));
        body.put("message", "Check de-activation failed");
        body.put("error", "true");
        return new ResponseEntity(body,HttpStatus.valueOf(200));
    }
}
