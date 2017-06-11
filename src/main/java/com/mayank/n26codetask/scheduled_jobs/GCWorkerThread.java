package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.entities.AppProperties;
import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.utilities.EpochConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by mayank.gupta on 10/06/17.
 */
public class GCWorkerThread implements Runnable {


    private TransactionManager manager;
    private Logger logger = LoggerFactory.getLogger(IncomingTnxConsumerThread.class);
    private Transaction dummyTx;
    private AppProperties appProps;

    @Autowired
    public void setManager(TransactionManager manager) {
        this.manager = manager;
    }

    @Autowired
    public void setAppProps(AppProperties appProps) {
        this.appProps = appProps;
    }

    @Override
    public void run() {

        long lastAllowedTimeStamp = EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec());
        removeOldTxAndUpdateSum(lastAllowedTimeStamp);
        updateMinMax(lastAllowedTimeStamp);
    }

    private void removeOldTxAndUpdateSum(long lastAllowedTimeStamp) {

        while(entryExpired(lastAllowedTimeStamp)) {

            Statistics stats = manager.sortedByTimeMap.pollFirstEntry().getValue();
            manager.sum.add(-stats.getSum());
            manager.count.addAndGet(-stats.getCount());

            logger.info("Expired transactions : " +  stats.getSum() + " Num of occurences : " + stats.getCount());

        }
    }



    private void updateMinMax(long lastAllowedTimeStamp) {

        ConcurrentSkipListSet<Transaction> sortedByAmt = manager.sortedByAmtSet;

        while(shouldRemoveHead(sortedByAmt,lastAllowedTimeStamp)) {
            Transaction minTx = sortedByAmt.pollFirst();
            logger.info("Removing min tx with amount from amount set :" + minTx.getAmount() + " : " + minTx.getEpoch());
        }

        while(shouldRemoveTail(sortedByAmt,lastAllowedTimeStamp)) {
            Transaction maxTx = sortedByAmt.pollLast();
            logger.info("Removing  max tx with amount from amount set :" + maxTx.getAmount() + " : " + maxTx.getEpoch());
        }
    }

    private boolean shouldRemoveHead(ConcurrentSkipListSet<Transaction> sortedSet, long timeFilter) {

        if(sortedSet.isEmpty())
            return false;

        return shouldRemove(sortedSet.first(),timeFilter);
    }

    private boolean shouldRemoveTail(ConcurrentSkipListSet<Transaction> sortedSet, long timeFilter) {

        if(sortedSet.isEmpty())
            return false;

        return shouldRemove(sortedSet.last(),timeFilter);
    }


    private boolean shouldRemove(Transaction tx, long timeFilter) {
        return tx.getEpoch() <= timeFilter;
    }

    private boolean entryExpired(long lastAllowedTimeStamp) {
        return  !manager.sortedByTimeMap.isEmpty() && manager.sortedByTimeMap.firstKey() < lastAllowedTimeStamp;
    }
}
