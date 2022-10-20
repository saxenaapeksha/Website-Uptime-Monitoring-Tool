package com.example.wup.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "check_scheduler")
public class CheckScheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String websiteURL;

    private Integer frequency;

    private String frequencyUnit;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    private Boolean active;

    private Long maxFailedAttempt;

    private String email;

    public CheckScheduler() {
    }

    public CheckScheduler(Long id, String name, String websiteURL, Integer frequency, String frequencyUnit, Date created, Date updated, Boolean active, Long maxFailedAttempt, String email) {
        this.id = id;
        this.name = name;
        this.websiteURL = websiteURL;
        this.frequency = frequency;
        this.frequencyUnit = frequencyUnit;
        this.created = created;
        this.updated = updated;
        this.active = active;
        this.maxFailedAttempt = maxFailedAttempt;
        this.email = email;
    }
}
