package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.entities.AppProperties;
import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.utilities.EpochConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
            updateSumAndCount(tx);
            addToSortedTimeMap(tx);
            manager.sortedByAmtSet.add(tx);



            logger.info("Final numbers are : Sum = " + manager.sum.doubleValue() + " , Count = " + manager.count.get()
                    + ", min = " + manager.sortedByAmtSet.first().getAmount() + ", max = "
                    + manager.sortedByAmtSet.last().getAmount());
        }
    }

    private void addToSortedTimeMap(Transaction tx) {

        long txTime = tx.getEpoch();
        double txAmount = tx.getAmount();

        for(Map.Entry<Long,Statistics> entry : manager.sortedByTimeMap.entrySet()) {
            logger.info("Time map :" + entry.getKey() + " : " +   entry.getValue().getCount() + " : " + entry.getValue().getSum());
        }

        if(manager.sortedByTimeMap.containsKey(txTime)) {
            logger.info("Adding to existing key");
            manager.sortedByTimeMap.get(txTime).addSum(tx.getAmount());
            manager.sortedByTimeMap.get(txTime).addCount((long)1);
        }else {
            logger.info("Adding new key");
            Statistics stats = new Statistics(txAmount,1);
            manager.sortedByTimeMap.put(txTime,stats);
        }
    }

    private void updateSumAndCount(Transaction tx) {

        manager.sum.add(tx.getAmount());
        manager.count.getAndIncrement();
    }

}
