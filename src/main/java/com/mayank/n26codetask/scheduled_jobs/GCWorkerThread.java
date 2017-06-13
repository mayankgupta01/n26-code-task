package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.config.AppProperties;
import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.utilities.EpochConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by mayank.gupta on 10/06/17.
 */
public class GCWorkerThread implements Runnable {

    @Autowired
    private TransactionManager manager;

    @Autowired
    private AppProperties appProps;

    private Logger logger = LoggerFactory.getLogger(IncomingTnxConsumerThread.class);

    @Override
    public void run() {

        long lastAllowedTimeStamp = EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec());
        updateMinMaxAmount(lastAllowedTimeStamp);
        removeExpiredTxAndUpdateTotalSum(lastAllowedTimeStamp);

    }

    private void removeExpiredTxAndUpdateTotalSum(long lastAllowedTimeStamp) {
        double expiredSum = 0;
        long expiredTxCount = 0;

        while(entryExpired(lastAllowedTimeStamp)) {

            Statistics stats = manager.sortedByTimeMap.pollFirstEntry().getValue();
         /*
         manager.sum.add(-stats.getSum());
         manager.count.addAndGet(-stats.getCount());
         */
            expiredSum += stats.getSum();
            expiredTxCount += stats.getCount();
            logger.info("Expired transactions summary : " +  stats.getSum() + " Count of txs : " + stats.getCount());

        }

        if(expiredTxCount != 0) {
            manager.setStatsSnapshot(-expiredSum,-expiredTxCount);
        }
    }



    private void updateMinMaxAmount(long lastAllowedTimeStamp) {

        ConcurrentSkipListSet<Transaction> sortedByAmt = manager.sortedByAmtSet;

        while(shouldRemoveHead(sortedByAmt,lastAllowedTimeStamp)) {
            Transaction minTx = sortedByAmt.pollFirst();
            logger.info("Expired min tx with amount from amount set :" + minTx.getAmount() + " : " + minTx.getEpoch());
        }

        while(shouldRemoveTail(sortedByAmt,lastAllowedTimeStamp)) {
            Transaction maxTx = sortedByAmt.pollLast();
            logger.info("Expired  max tx with amount from amount set :" + maxTx.getAmount() + " : " + maxTx.getEpoch());
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
