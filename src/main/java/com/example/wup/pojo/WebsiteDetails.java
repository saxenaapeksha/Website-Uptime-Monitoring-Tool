package com.example.wup.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class WebsiteDetails {
    String status;
    Date upTime;
    Date downTime;
    String avgResponseTime;

    public WebsiteDetails() {
    }

    public WebsiteDetails(String status, Date upTime, Date downTime, String avgResponseTime) {
        this.status = status;
        this.upTime = upTime;
        this.downTime = downTime;
        this.avgResponseTime = avgResponseTime;
    }
}
