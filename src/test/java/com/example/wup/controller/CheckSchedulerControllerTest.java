package com.example.wup.controller;

import com.example.wup.exception.FrequencyOutOfRangeException;
import com.example.wup.model.CheckScheduler;
import com.example.wup.model.Website;
import com.example.wup.repository.CheckSchedulerRepository;
import com.example.wup.repository.WebsiteRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class CheckSchedulerControllerTest {
    CheckSchedulerRepository checkSchedulerRepository;
    WebsiteRepository websiteRepository;
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
        checkSchedulerRepository = mock(CheckSchedulerRepository.class);
        websiteRepository = mock(WebsiteRepository.class);
        checkSchedulerController = new CheckSchedulerController(checkSchedulerRepository, websiteRepository);
    }

    @Test
    public void testFindAll() {
        when(checkSchedulerRepository.findAll()).thenReturn(populateCheckScheduler());
        ResponseEntity responseEntity = checkSchedulerController.findAll();
        Assert.assertNotNull(responseEntity);
        ArrayList<CheckScheduler> body = (ArrayList<CheckScheduler>) responseEntity.getBody();
        Assert.assertEquals(body.size(), 1);
        Assert.assertEquals(body.get(0).getName(), "Google");
    }

    @Test
    public void testRegisterSuccess() {
        CheckScheduler checkObject = populateCheckScheduler().get(0);
        when(checkSchedulerRepository.save(checkObject)).thenReturn(populateCheckScheduler().get(0));
        Website website = populateWebsiteObject(checkObject);
        when(websiteRepository.save(any(Website.class))).thenReturn(website);
        ResponseEntity responseEntity = checkSchedulerController.register(checkObject);
        Assert.assertNotNull(responseEntity);
        com.example.wup.pojo.CheckScheduler body = (com.example.wup.pojo.CheckScheduler) responseEntity.getBody();
        Assert.assertNotNull(body);
        Assert.assertEquals(body.getWebsiteURL(), "https://www.google.com");
    }

    @Test
    public void testRegisterFail() {
        CheckScheduler checkObject = populateCheckScheduler().get(0);
        when(checkSchedulerRepository.save(checkObject)).thenReturn(null);
        ResponseEntity responseEntity = checkSchedulerController.register(checkObject);
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(body.get("message"), "register new check failed");
        Assert.assertEquals(body.get("error"), "true");
    }

    @Test
    public void testFilterByFrequency() throws FrequencyOutOfRangeException {
        int frequency = 1;
        String unit = "minute";
        when(checkSchedulerRepository.filterByFrequency(frequency, unit)).thenReturn(populateCheckScheduler());
        ResponseEntity<List<CheckScheduler>> responseEntity = checkSchedulerController.filter("1minute", null);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(responseEntity.getBody().size(), 1);
        Assert.assertNotNull(responseEntity.getBody().get(0));
        Assert.assertEquals(responseEntity.getBody().get(0).getWebsiteURL(), "https://www.google.com");
    }

    @Test
    public void testFilterByName() throws FrequencyOutOfRangeException {
        String name = "Google";
        when(checkSchedulerRepository.filterByName(name)).thenReturn(populateCheckScheduler());
        ResponseEntity<List<CheckScheduler>> responseEntity = checkSchedulerController.filter(null, "Google");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(responseEntity.getBody().size(), 1);
        Assert.assertNotNull(responseEntity.getBody().get(0));
        Assert.assertEquals(responseEntity.getBody().get(0).getWebsiteURL(), "https://www.google.com");
    }

    @Test
    public void testFilterByNameAndFrequency() throws FrequencyOutOfRangeException {
        int frequency = 1;
        String unit = "minute";
        String name = "Google";
        when(checkSchedulerRepository.filterByFrequency(frequency, unit)).thenReturn(populateCheckScheduler());
        ResponseEntity<List<CheckScheduler>> responseFilterEntity = checkSchedulerController.filter("1minute", null);
        when(checkSchedulerRepository.filterByName(name)).thenReturn(populateCheckScheduler());
        ResponseEntity<List<CheckScheduler>> responseNameEntity = checkSchedulerController.filter(null, "Google");
        Assert.assertNotNull(responseFilterEntity);
        Assert.assertEquals(responseFilterEntity.getBody().size(), 1);
        Assert.assertNotNull(responseFilterEntity.getBody().get(0));
        Assert.assertEquals(responseFilterEntity.getBody().get(0).getWebsiteURL(), "https://www.google.com");
    }

    @Test
    public void testFilterByNull() throws FrequencyOutOfRangeException {
        int frequency = 1;
        String unit = "minute";
        String name = "Google";
        when(checkSchedulerRepository.filterByFrequency(frequency, unit)).thenReturn(null);
        ResponseEntity<List<CheckScheduler>> responseFilterEntity = checkSchedulerController.filter(null, null);
        when(checkSchedulerRepository.filterByName(name)).thenReturn(null);
        ResponseEntity<List<CheckScheduler>> responseNameEntity = checkSchedulerController.filter(null, null);
        Map<String, String> responseNameBody = (Map<String, String>) responseNameEntity.getBody();
        Assert.assertEquals(responseNameBody.get("message"), "Both frequency and name filters are null, either one of the request param is mandatory.");
        Assert.assertEquals(responseNameBody.get("error"), "true");
        Map<String, String> responseFilterBody = (Map<String, String>) responseFilterEntity.getBody();
        Assert.assertEquals(responseFilterBody.get("message"), "Both frequency and name filters are null, either one of the request param is mandatory.");
        Assert.assertEquals(responseFilterBody.get("error"), "true");
    }

    @Test
    public void testActivateSuccess() {
        int id = 1;
        when(checkSchedulerRepository.updateStatus(Boolean.TRUE, 1)).thenReturn(1);
        ResponseEntity responseEntity = checkSchedulerController.activate(id);
        Assert.assertNotNull(responseEntity);
        Map<String, String> responseFilterBody = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(responseFilterBody.get("message"), "Check Activated");
        Assert.assertEquals(responseFilterBody.get("success"), "true");
    }

    @Test
    public void testActivateFail() {
        int id = 2;
        when(checkSchedulerRepository.updateStatus(Boolean.TRUE, 1)).thenReturn(0);
        ResponseEntity responseEntity = checkSchedulerController.activate(id);
        Assert.assertNotNull(responseEntity);
        Map<String, String> responseFilterBody = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(responseFilterBody.get("message"), "Check activation failed");
        Assert.assertEquals(responseFilterBody.get("error"), "true");
    }

    @Test
    public void testDeActivateSuccess() {
        int id = 1;
        when(checkSchedulerRepository.updateStatus(Boolean.TRUE, 1)).thenReturn(1);
        ResponseEntity responseEntity = checkSchedulerController.deactivate(id);
        Assert.assertNotNull(responseEntity);
        Map<String, String> responseFilterBody = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(responseFilterBody.get("message"), "Check Deactivated");
        Assert.assertEquals(responseFilterBody.get("success"), "true");
    }

    @Test
    public void testDeActivateFail() {
        int id = 2;
        when(checkSchedulerRepository.updateStatus(Boolean.TRUE, 1)).thenReturn(0);
        ResponseEntity responseEntity = checkSchedulerController.deactivate(id);
        Assert.assertNotNull(responseEntity);
        Map<String, String> responseFilterBody = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(responseFilterBody.get("message"), "Check de-activation failed");
        Assert.assertEquals(responseFilterBody.get("error"), "true");
    }

}
