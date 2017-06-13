package com.mayank.n26codetask.services;

import com.mayank.n26codetask.config.AppProperties;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.scheduled_jobs.GCWorkerThread;
import com.mayank.n26codetask.scheduled_jobs.IncomingTnxConsumerThread;
import com.mayank.n26codetask.scheduled_jobs.TransactionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mayank.gupta on 12/06/17.
 */
public class TransactionServiceTest {

    @Mock
    private TransactionManager txManager = mock(TransactionManager.class);

    @Mock
    private AppProperties appProps = mock(AppProperties.class);

    @Mock
    private IncomingTnxConsumerThread incomingTnxConsumerThread = mock(IncomingTnxConsumerThread.class);

    @Mock
    private GCWorkerThread gcWorkerThread = mock(GCWorkerThread.class);

    @InjectMocks
    private TransactionService txService = new TransactionService(incomingTnxConsumerThread,gcWorkerThread);

    private Transaction txWithCurrentTimeStamp;
    private Transaction txWithExpiredTimeStamp;

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);

        txWithCurrentTimeStamp = new Transaction();
        txWithCurrentTimeStamp.setEpoch(System.currentTimeMillis());
        txWithCurrentTimeStamp.setAmount(100);

        txWithExpiredTimeStamp = new Transaction();
    }

    @Test
    public void shouldReturnFalseForInsertExpiredTransaction() {

        when(appProps.getCalculateStatsForSec()).thenReturn(60);
        txWithExpiredTimeStamp.setEpoch(System.currentTimeMillis() - appProps.getCalculateStatsForSec()*1000 - 1);

        Assert.assertEquals(txService.insert(txWithExpiredTimeStamp),false);
    }

    @Test
    public void shouldReturnTrueForInsertCurrentTransaction() {

        when(appProps.getCalculateStatsForSec()).thenReturn(60);

        Assert.assertEquals(txService.insert(txWithCurrentTimeStamp),true);

    }


}
