package com.example.wup.controller;

import com.example.wup.model.Website;
import com.example.wup.repository.CheckSchedulerRepository;
import com.example.wup.repository.WebsiteRepository;
import com.example.wup.pojo.WebsiteDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/website")
public class WebsiteController {
    private final CheckSchedulerRepository checkRepository;
    private final WebsiteRepository websiteRepository;

    public WebsiteController(CheckSchedulerRepository checkRepository, WebsiteRepository websiteRepository) {
        this.checkRepository = checkRepository;
        this.websiteRepository = websiteRepository;
    }

    private static Date getUpTime(Website websiteObject, String up) {
        return websiteObject.getStatus().equalsIgnoreCase(up) ?
                websiteObject.getLastStatusChange() : null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity status(@RequestParam String websiteURL) {
        Map<String, String> body = new HashMap<>();
        Integer id = checkRepository.existByURL(websiteURL);
        if (id == null) {
            body.put("message", "website is not registered");
            body.put("error", "true");
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        }
        Website websiteObject = websiteRepository.getDetails(id);

        if (websiteObject.getLastStatusChange() == null) {
            body.put("message", "scheduler has not yet executed");
            body.put("error", "true");
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        }
        Long hits = websiteObject.getTotalResponseTime() / websiteObject.getHits();
        WebsiteDetails websiteDetails = new WebsiteDetails(websiteObject.getStatus(),
                getUpTime(websiteObject, "up"),
                getUpTime(websiteObject, "down"), hits + "ms");
        return ResponseEntity.ok(websiteDetails);
    }

}
