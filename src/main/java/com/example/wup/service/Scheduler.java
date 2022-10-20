package com.example.wup.service;

import com.example.wup.model.CheckScheduler;
import com.example.wup.model.Website;
import com.example.wup.repository.CheckSchedulerRepository;
import com.example.wup.repository.WebsiteRepository;
import com.example.wup.utility.Utility;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class Scheduler {

    private final WebsiteRepository websiteRepository;
    private final CheckSchedulerRepository checkSchedulerRepository;

    public Scheduler(WebsiteRepository websiteRepository, CheckSchedulerRepository checkSchedulerRepository) {
        this.websiteRepository = websiteRepository;
        this.checkSchedulerRepository = checkSchedulerRepository;
    }

    private static long responseTimeInMillis(Website website, HealthCheck healthCheck) {
        long responseTimeInMillis = healthCheck.getResponseTimeInMillis();
        if (website.getTotalResponseTime() != null)
            responseTimeInMillis += website.getTotalResponseTime();
        return responseTimeInMillis;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkScheduler() {
        Calendar calPrev = Calendar.getInstance();
        Calendar calNext = Calendar.getInstance();
        calPrev.add(Calendar.MINUTE, -1);
        calNext.add(Calendar.MINUTE, 1);
        List<Website> websites = websiteRepository.getEligibleSchedules(calPrev.getTime(), calNext.getTime());
        Calendar currentTime = Calendar.getInstance();
        for (Website website : websites) {
            CheckScheduler checkScheduler = website.getCheckScheduler();
            HealthCheck healthCheck = new HealthCheck();
            healthCheck.perform(checkScheduler.getWebsiteURL());
            website.setNextRun(Utility.getNextRun(checkScheduler));
            setLastStatusTime(currentTime, healthCheck.getStatus(), website);
            website.setLastRun(currentTime.getTime());
            long hits = website.getHits() == null ? 0 : website.getHits();
            website.setHits(hits + 1);
            website.setTotalResponseTime(responseTimeInMillis(website, healthCheck));
            website.setStatus(healthCheck.getStatus());
            if (healthCheck.getStatus().equalsIgnoreCase("down"))
                website.setFailedAttempts(website.getFailedAttempts() + 1);
            websiteRepository.save(website);
        }
    }

    private void setLastStatusTime(Calendar currentTime, String status, Website website) {
        if (website.getLastStatusChange() == null || !status.equalsIgnoreCase(website.getStatus())) {
            website.setLastStatusChange(currentTime.getTime());
            if (status.equalsIgnoreCase("up"))
                website.setFailedAttempts(0L);
        }
    }
}




















