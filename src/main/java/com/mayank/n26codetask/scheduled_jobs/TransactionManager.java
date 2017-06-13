package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.entities.TransactionComparator;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;


/**
 * Created by mayank.gupta on 10/06/17.
 */
@Service
public class TransactionManager {

    // Queue to store incoming transactions
    protected final Queue<Transaction> incomingTxQueue;

    // Ordered Map of transactions by timestamp bucketed by each millisecond. Key = epoch, Value = Aggregated stats for the millisecond
    // This will ensure that max number of keys at any time will be 60k, ensuring worse time complexity of logn for maintaining
    // the DS.
    protected final ConcurrentSkipListMap<Long,Statistics> sortedByTimeMap;

    // Ordered set of transactions by transaction amount to calculate min and max. Transaction with same timestamp and amount
    // are considered duplicate
    protected final ConcurrentSkipListSet<Transaction> sortedByAmtSet;

    // DS to maintain history of all transactions inserted to service
    protected final List<Transaction> txHistory;


    private Statistics statsSnapshot;

    public TransactionManager() {
        incomingTxQueue = new ConcurrentLinkedQueue<>();
        sortedByTimeMap = new ConcurrentSkipListMap<>();
        sortedByAmtSet = new ConcurrentSkipListSet<>(TransactionComparator.AMOUNT);
        txHistory = new LinkedList<>();
        statsSnapshot = new Statistics.Builder(0,0)
                .setMinAmount(0)
                .setMaxAmount(0)
                .build();

    }


    public void addTransaction(Transaction tx) {
        incomingTxQueue.add(tx);
        addToTransactionHistory(tx);
    }

    public void addToTransactionHistory(Transaction tx) {
        txHistory.add(tx);
    }

    public Statistics getStatsSnapShot() {
        return statsSnapshot;
    }

    protected synchronized void setStatsSnapshot(double deltaAmount, long deltaCount) {

        double newSum = statsSnapshot.getSum()+deltaAmount;
        long newCOunt = statsSnapshot.getCount() + deltaCount;

        statsSnapshot = new Statistics.Builder(newSum,newCOunt).setMinAmount(getTransactionMinAmount())
                .setMaxAmount(getTransactionMaxAmount())
                .build();
    }

    private double getTransactionMinAmount() {
        double minAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            minAmount = sortedByAmtSet.first().getAmount();
        }

        return minAmount;
    }


    private double getTransactionMaxAmount() {
        double maxAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            maxAmount = sortedByAmtSet.last().getAmount();
        }

        return maxAmount;
    }
}
