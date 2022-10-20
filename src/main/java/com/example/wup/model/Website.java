package com.example.wup.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "website")
public class Website {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "checkId", nullable = false)
    private CheckScheduler checkScheduler;

    private String status;

    private Date lastRun;

    private Date nextRun;

    private Date lastStatusChange;

    private Long totalResponseTime;

    private Long hits;

    private Long failedAttempts;

    public Website() {
    }

    public Website(Long id, CheckScheduler checkScheduler, String status, Date lastRun, Date nextRun, Date lastStatusChange, Long totalResponseTime, Long hits, Long failedAttempts) {
        this.id = id;
        this.checkScheduler = checkScheduler;
        this.status = status;
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.lastStatusChange = lastStatusChange;
        this.totalResponseTime = totalResponseTime;
        this.hits = hits;
        this.failedAttempts = failedAttempts;
    }

    @PostUpdate
    private void postUpdate() {
        if (this.status.equalsIgnoreCase("down")) {
            if (this.failedAttempts == this.checkScheduler.getMaxFailedAttempt()) {
                System.out.println("Notifying to " + checkScheduler.getEmail() + "\n--------------------\n" +
                        " Website " + checkScheduler.getWebsiteURL() + " is Down since " + lastStatusChange);
            }
        }
    }
}
