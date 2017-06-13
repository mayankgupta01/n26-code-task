package com.mayank.n26codetask.services;

import com.mayank.n26codetask.config.AppProperties;
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

    @Autowired
    private TransactionManager manager;

    @Autowired
    private AppProperties appProps;

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(IncomingTnxConsumerThread adder, GCWorkerThread maintainer) {
        executor = Executors.newScheduledThreadPool(2);
        this.adder = adder;
        this.maintainer = maintainer;
    }

    @PostConstruct
    public void startWorkerThreads() {
        executor.scheduleWithFixedDelay(maintainer,100,appProps.getGcIntervalInMilliSec(),TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(adder,0,100,TimeUnit.MILLISECONDS);
    }

    public boolean insert(Transaction tx) {

        if(EpochConverter.getPastTimestamp(appProps.getCalculateStatsForSec()) > tx.getEpoch()) {
            logger.info("Transaction is older than 60 secs, addintg to history, discarding from stats calculation !");
            manager.addToTransactionHistory(tx);
            return false;
        }

        manager.addTransaction(tx);
        return true;
    }

    public Statistics getStatistics() {
        return manager.getStatsSnapShot();
    }


}
