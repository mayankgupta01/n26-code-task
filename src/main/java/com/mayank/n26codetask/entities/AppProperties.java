package com.mayank.n26codetask.entities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by mayank.gupta on 11/06/17.
 */
@Component
@ConfigurationProperties("app") // prefix app, find app.* values
public class AppProperties {

    private int calculateStatsForSec;
    private int gcIntervalInMilliSec;

    public int getCalculateStatsForSec() {
        return calculateStatsForSec;
    }

    public void setCalculateStatsForSec(int calculateStatsForSec) {
        this.calculateStatsForSec = calculateStatsForSec;
    }

    public int getGcIntervalInMilliSec() {
        return gcIntervalInMilliSec;
    }

    public void setGcIntervalInMilliSec(int gcIntervalInMilliSec) {
        this.gcIntervalInMilliSec = gcIntervalInMilliSec;
    }
}
