package com.example.wup.utility;

import com.example.wup.exception.FrequencyOutOfRangeException;
import com.example.wup.model.CheckScheduler;
import com.example.wup.pojo.Frequency;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    static final long ONE_MINUTE_IN_MILLIS = 60000;
    static final long ONE_HOUR_IN_MILLIS = 3600000;


    public static Frequency validateFrequency(String frequency) throws FrequencyOutOfRangeException {
        Frequency frequencyObj = new Frequency();
        int frequencyNumber = getFrequencyNumber(frequency);
        frequencyObj.setNumber(frequencyNumber);
        String frequencyUnit = getFrequencyUnit(frequency);
        frequencyObj.setUnit(frequencyUnit);
        if (frequencyUnit.equals("hour") && frequencyNumber > 24) {
            throw new FrequencyOutOfRangeException("Frequency Type out of range");
        } else if (frequencyUnit.equals("minute") && frequencyNumber > 59) {
            throw new FrequencyOutOfRangeException("Frequency Type out of range");
        }
        return frequencyObj;
    }

    public static int getFrequencyNumber(String frequency) {
        return Integer.parseInt(frequency.replaceAll("[^0-9]", ""));
    }

    public static String getFrequencyUnit(String frequency) {
        return frequency.replaceAll("[^a-z,A-Z]*", "");
    }

    public static Timestamp getCurrentTimestamp() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public static Date getNextRun(CheckScheduler checkScheduler) {
        Calendar currentTime = Calendar.getInstance();
        Date after = null;
        int frequency = checkScheduler.getFrequency();
        long curTimeInMs = currentTime.getTime().getTime();
        if (checkScheduler.getFrequencyUnit().equals("minute"))
            after = new Date(curTimeInMs + (frequency * ONE_MINUTE_IN_MILLIS));
        else
            after = new Date(curTimeInMs + (frequency * ONE_HOUR_IN_MILLIS));
        return after;
    }
}
