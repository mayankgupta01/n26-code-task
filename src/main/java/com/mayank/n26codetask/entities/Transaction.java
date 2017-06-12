package com.mayank.n26codetask.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by mayank.gupta on 10/06/17.
 */
public class Transaction {


    @NotNull
    @Valid
    @JsonProperty(value = "amount", required = true)
    private double amount;


    @NotNull
    @Valid
    @JsonProperty(value = "timestamp", required = true)
    private long epoch;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Transaction) {
            return ((Transaction) obj).getEpoch() == amount && ((Transaction) obj).getEpoch() == epoch;
        }

        return false;
    }


    @Override
    public String toString() {
        return "Transaction time : " + epoch + "\nTransaction amount : " + amount;
    }
}
