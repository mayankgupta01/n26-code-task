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

    // Ordered Map of transactions by timestamp bucketed by each millisecond. Key = epoch, Value = Aggregated stats for the millisecond
    // This will ensure that max number of keys at any time will be 60k, ensuring worse time complexity of logn for maintaining
    // the DS.
    protected final ConcurrentSkipListMap<Long,Statistics> sortedByTimeMap;

    // Ordered set of transactions by transaction amount to calculate min and max. Transaction with same timestamp and amount
    // are considered duplicate
    protected final ConcurrentSkipListSet<Transaction> sortedByAmtSet;

    // DS to maintain history of all transactions inserted to service
    protected final List<Transaction> txHistory;

    //Atomic double to ensure threadsafe view of sum
    protected DoubleAdder sum;

    //AtomicLong to ensure threadsafe view of count
    protected AtomicLong count;

    public TransactionManager() {
        incomingTxQueue = new ConcurrentLinkedQueue<>();
        sortedByTimeMap = new ConcurrentSkipListMap<>();
        sortedByAmtSet = new ConcurrentSkipListSet<>(TransactionComparator.AMOUNT);
        txHistory = new LinkedList<>();
        this.sum = new DoubleAdder();
        this.count = new AtomicLong(0);
    }


    public void addTransaction(Transaction tx) {
        incomingTxQueue.add(tx);
        txHistory.add(tx);
    }


    public double getTransactionsAmountTotal() {
        return sum.doubleValue();
    }


    public long getTransactionsCount() {
        return count.get();
    }


    public double getTransactionMinAmount() {
        double minAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            minAmount = sortedByAmtSet.first().getAmount();
        }

        return minAmount;
    }


    public double getTransactionMaxAmount() {
        double maxAmount = 0;

        if(!sortedByAmtSet.isEmpty()) {
            maxAmount = sortedByAmtSet.last().getAmount();
        }

        return maxAmount;
    }

}
