package com.mayank.n26codetask.utilities;

/**
 * Created by mayank.gupta on 11/06/17.
 */
public class EpochConverter {

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static long getPastTimestamp(int timeInSeconds) {
        return getCurrentTime() - timeInSeconds*1000;
    }
}
