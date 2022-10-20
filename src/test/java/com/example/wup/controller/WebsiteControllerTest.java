package com.example.wup.controller;

import com.example.wup.model.CheckScheduler;
import com.example.wup.model.Website;
import com.example.wup.repository.CheckSchedulerRepository;
import com.example.wup.repository.WebsiteRepository;
import com.example.wup.pojo.WebsiteDetails;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class WebsiteControllerTest {
    CheckSchedulerRepository checkSchedulerRepository;
    WebsiteRepository websiteRepository;
    WebsiteController websiteController;
    CheckSchedulerController checkSchedulerController;

    private static List<CheckScheduler> populateCheckScheduler() {
        List<CheckScheduler> checkSchedulers = new ArrayList<>();
        CheckScheduler checkScheduler = new CheckScheduler();
        checkScheduler.setEmail("net.apeksha@gmail.com");
        checkScheduler.setActive(true);
        checkScheduler.setId(1L);
        checkScheduler.setMaxFailedAttempt(3L);
        checkScheduler.setCreated(new Date());
        checkScheduler.setName("Google");
        checkScheduler.setFrequency(1);
        checkScheduler.setFrequencyUnit("minute");
        checkScheduler.setUpdated(new Date());
        checkScheduler.setWebsiteURL("https://www.google.com");
        checkSchedulers.add(checkScheduler);
        return checkSchedulers;
    }

    private Website populateWebsiteObject(CheckScheduler checkObject) {
        Website website = new Website();
        website.setCheckScheduler(checkObject);
        website.setHits(1L);
        website.setFailedAttempts(1L);
        website.setNextRun(new Date());
        website.setLastRun(new Date());
        website.setTotalResponseTime(500L);
        website.setLastStatusChange(new Date());
        website.setId(1L);
        website.setStatus("up");
        return website;
    }

    @BeforeEach
    public void setup() {
        this.checkSchedulerRepository = mock(CheckSchedulerRepository.class);
        this.websiteRepository = mock(WebsiteRepository.class);
        this.checkSchedulerController = new CheckSchedulerController(checkSchedulerRepository, websiteRepository);
        this.websiteController = new WebsiteController(checkSchedulerRepository, websiteRepository);
    }

    @Test
    public void testStatusSuccess() {
        int checkId = 1;
        CheckScheduler checkScheduler = populateCheckScheduler().get(0);
        Website website = populateWebsiteObject(checkScheduler);
        String websiteURL = "https://www.google.com";
        when(this.checkSchedulerRepository.existByURL(websiteURL)).thenReturn(checkId);
        when(this.websiteRepository.getDetails(checkId)).thenReturn(website);
        Long hits = website.getTotalResponseTime() / website.getHits();
        WebsiteDetails websiteDetails = new WebsiteDetails("up", new Date(), null, hits + "ms");
        ResponseEntity<WebsiteDetails> responseEntity = websiteController.status(websiteURL);
        Assert.assertNotNull(responseEntity);
    }

    @Test
    public void testStatusWhenNullURL() {
        int checkId = 1;
        CheckScheduler checkScheduler = populateCheckScheduler().get(0);
        Website website = populateWebsiteObject(checkScheduler);
        String websiteURL = "https://www.google.com";
        when(checkSchedulerRepository.existByURL(websiteURL)).thenReturn(null);
        ResponseEntity responseEntity = websiteController.status(websiteURL);
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(body.get("message"), "website is not registered");
        Assert.assertEquals(body.get("error"), "true");
    }

    @Test
    public void testStatusWhenNullLastStatusChange() {
        int checkId = 1;
        CheckScheduler checkScheduler = populateCheckScheduler().get(0);
        Website website = populateWebsiteObject(checkScheduler);
        website.setLastStatusChange(null);
        String websiteURL = "https://www.google.com";
        when(checkSchedulerRepository.existByURL(websiteURL)).thenReturn(1);
        when(websiteRepository.getDetails(checkId)).thenReturn(website);
        ResponseEntity responseEntity = websiteController.status(websiteURL);
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(body.get("message"), "scheduler has not yet executed");
        Assert.assertEquals(body.get("error"), "true");
    }
}
