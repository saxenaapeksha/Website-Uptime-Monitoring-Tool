package com.example.wup.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HealthCheck {

    private long responseTimeInMillis;
    private String status;

    public long getResponseTimeInMillis() {
        return responseTimeInMillis;
    }

    public String getStatus() {
        return status;
    }

    public void perform(String websiteUrl) {
        try {
            URL obj = new URL(websiteUrl);
            long start = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("GET");
            int responseCode = 0;
            responseCode = con.getResponseCode();
            long end = System.currentTimeMillis();
            responseTimeInMillis = end - start;
            System.out.println("GET Response Code :: " + responseCode);
            status = (responseCode == HttpURLConnection.HTTP_OK ? "up" : "down");
        } catch (IOException e) {
            status = "down";
        }
    }
}
