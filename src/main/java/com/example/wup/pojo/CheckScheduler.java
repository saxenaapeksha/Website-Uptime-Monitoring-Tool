package com.example.wup.pojo;

import lombok.Data;

@Data
public class CheckScheduler {
    private Long id;

    private String name;

    private String websiteURL;

    private Integer frequency;

    private String frequencyUnit;
}
