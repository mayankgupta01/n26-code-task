package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.config.AppProperties;
import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.utilities.EpochConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mayank.gupta on 10/06/17.
 */
public class IncomingTnxConsumerThread implements Runnable {

    @Autowired
    private TransactionManager manager;

    @Autowired
    private AppProperties appProps;

    private Logger logger = LoggerFactory.getLogger(IncomingTnxConsumerThread.class);

    @Override
    public void run() {

        while(!manager.incomingTxQueue.isEmpty()) {

            updateStatistics(manager.incomingTxQueue.poll());
        }
    }

    private void updateStatistics(Transaction tx) {

        if(tx.getEpoch() > EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec())) {
            manager.sortedByAmtSet.add(tx);
            updateSumAndCount(tx);
            addToSortedTimeMap(tx);
        }
    }

    private void addToSortedTimeMap(Transaction tx) {

        long txTime = tx.getEpoch();
        double txAmount = tx.getAmount();

        if(manager.sortedByTimeMap.containsKey(txTime)) {
            logger.info("Adding to existing key :" + txTime +  ", " + txAmount);
            manager.sortedByTimeMap.get(txTime).addSum(tx.getAmount());
            manager.sortedByTimeMap.get(txTime).addCount((long)1);
        }else {
            logger.info("Adding new key :" + txTime +  ", " + txAmount);
            Statistics stats =  new Statistics.Builder(txAmount,1).build();
            manager.sortedByTimeMap.put(txTime,stats);
        }
    }

    private void updateSumAndCount(Transaction tx) {

        /*manager.sum.add(tx.getAmount());
        manager.count.getAndIncrement();
        */
        manager.setStatsSnapshot(tx.getAmount(),1);
    }

}
