package com.mayank.n26codetask.services;

import com.mayank.n26codetask.entities.*;
import com.mayank.n26codetask.scheduled_jobs.GCWorkerThread;
import com.mayank.n26codetask.scheduled_jobs.IncomingTnxConsumerThread;
import com.mayank.n26codetask.scheduled_jobs.TransactionManager;
import com.mayank.n26codetask.utilities.EpochConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mayank.gupta on 10/06/17.
 */
@Service
public class TransactionService {

    private final ScheduledExecutorService executor;
    private final IncomingTnxConsumerThread adder;
    private final GCWorkerThread maintainer;
    private final TransactionManager manager;
    private AppProperties appProps;
    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(IncomingTnxConsumerThread adder, GCWorkerThread maintainer, TransactionManager manager) {
        executor = Executors.newScheduledThreadPool(2);
        this.adder = adder;
        this.maintainer = maintainer;
        this.manager = manager;
    }

    @Autowired
    public void setAppProps(AppProperties appProps) {
        this.appProps = appProps;
    }

    @PostConstruct
    public void startWorkerThreads() {
        executor.scheduleWithFixedDelay(maintainer,100,appProps.getGcIntervalInMilliSec(),TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(adder,0,100,TimeUnit.MILLISECONDS);
    }

    public boolean insert(Transaction tx) {
        logger.info(" PAST TIMESTAMP : " + EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec()) + " : " + System.currentTimeMillis() +" , Transaction Timestamp : " + tx.getEpoch());
        if(EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec()) > tx.getEpoch()) {
            logger.info("Transaction is older than 60 secs");
            return false;
        }

        manager.addToInboundTxQueue(tx);
        return true;
    }

    public Statistics getStatistics() {
        double min = manager.getMinAmount();
        double max = manager.getMaxAmount();
        double sum = manager.getSum();
        long count = manager.getCount();
        double avg = count == 0 ? 0 : sum/count;

        Statistics statistics = new Statistics(sum,count);
        statistics.setMinAmount(min);
        statistics.setMaxAmount(max);
        statistics.setAverage(avg);

        return statistics;
    }


}
