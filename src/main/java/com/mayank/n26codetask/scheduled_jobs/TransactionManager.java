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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;


/**
 * Created by mayank.gupta on 10/06/17.
 */
@Service
public class TransactionManager {

    protected final Queue<Transaction> incomingTxQueue;
    // Change this to protected after debugging
    protected final ConcurrentSkipListMap<Long,Statistics> sortedByTimeMap;
    protected final ConcurrentSkipListSet<Transaction> sortedByAmtSet;
    protected final List<Transaction> txHistory;

    protected DoubleAdder sum;
    protected AtomicLong count;

    public TransactionManager() {
        incomingTxQueue = new ConcurrentLinkedQueue<>();
        sortedByTimeMap = new ConcurrentSkipListMap<>();
        sortedByAmtSet = new ConcurrentSkipListSet<>(TransactionComparator.AMOUNT);
        txHistory = new LinkedList<>();
        this.sum = new DoubleAdder();
        this.count = new AtomicLong(0);
    }

    public void addToInboundTxQueue(Transaction tx) {
        incomingTxQueue.add(tx);
        txHistory.add(tx);
    }


    public double getSum() {
        return sum.doubleValue();
    }

    public long getCount() {
        return count.get();
    }

    public double getMinAmount() {
        double minAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            minAmount = sortedByAmtSet.first().getAmount();
        }

        return minAmount;
    }

    public double getMaxAmount() {
        double maxAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            maxAmount = sortedByAmtSet.last().getAmount();
        }

        return maxAmount;
    }


}
