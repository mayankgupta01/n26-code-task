package com.mayank.n26codetask.config;

import com.mayank.n26codetask.scheduled_jobs.IncomingTnxConsumerThread;
import com.mayank.n26codetask.scheduled_jobs.GCWorkerThread;
import com.mayank.n26codetask.scheduled_jobs.TransactionManager;
import com.mayank.n26codetask.services.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mayank.gupta on 10/06/17.
 */

@Configuration
@ComponentScan({"com.mayank.n26codetask"})

public class ApplicationConfig {

    @Bean
    public TransactionService transactionService() {
        return new TransactionService(incomingTnxConsumer(),maintainerWorker(),transactionManager());
    }

    @Bean
    public TransactionManager transactionManager() {
        return new TransactionManager();
    }

    @Bean
    public IncomingTnxConsumerThread incomingTnxConsumer() {
        return new IncomingTnxConsumerThread();
    }

    @Bean
    public GCWorkerThread maintainerWorker() {
        return new GCWorkerThread();
    }

}
