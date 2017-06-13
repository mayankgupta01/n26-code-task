package com.mayank.n26codetask.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Created by mayank.gupta on 11/06/17.
 */
public class Statistics {

    @NotNull
    @JsonProperty(value = "sum", required = true)
    private DoubleAdder sum;

    @NotNull
    @JsonProperty(value = "count", required = true)
    private AtomicLong count;

    @NotNull
    @JsonProperty(value = "avg", required = true)
    private double average;

    @NotNull
    @JsonProperty(value = "min", required = true)
    private double minAmount;

    @NotNull
    @JsonProperty(value = "max", required = true)
    private double maxAmount;

    private Statistics(Builder builder) {
        this.sum = builder.sum;
        this.count = builder.count;
        this.average = builder.average;
        this.maxAmount = builder.maxAmount;
        this.minAmount = builder.minAmount;
    }



    public double getSum() {
        return sum.doubleValue();
    }

    public void addSum(double sum) {
        this.sum.add(sum);
    }

    public long getCount() {
        return count.longValue();
    }

    public void addCount(Long count) {
        this.count.addAndGet(count);
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }


    public static class Builder {


        private DoubleAdder sum;

        private AtomicLong count;

        private double average;

        private double minAmount;

        private double maxAmount;

        public Builder(double sum, long count) {
            this.sum = new DoubleAdder();
            this.count = new AtomicLong(0);
            this.sum.add(sum);
            this.count.addAndGet(count);

            this.average = sum == 0 || count == 0 ? 0 : sum/count;
        }

        public Builder setMinAmount(double minAmount) {
            this.minAmount = minAmount;
            return this;
        }

        public Builder setMaxAmount(double maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public Statistics build() {
            return new Statistics(this);
        }
    }
}
