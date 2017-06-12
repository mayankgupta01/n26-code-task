/*
package com.mayank.n26codetask.scheduled_jobs;

import com.mayank.n26codetask.entities.AppProperties;
import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

*/
/**
 * Created by mayank.gupta on 12/06/17.
 *//*

public class IncomingTnxConsumerThreadTest {

    @Mock
    private TransactionManager txManager = mock(TransactionManager.class);

    @Mock
    private AppProperties appProps;

    @InjectMocks
    private IncomingTnxConsumerThread incomingTnxConsumerThread = new IncomingTnxConsumerThread();

    private Transaction txWithExpiredTimeStamp;
    private Transaction txWithCurrentTimeStamp;
    private ConcurrentSkipListMap<Long,Statistics> txMap = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListSet<Transaction> txAmtSet = new ConcurrentSkipListSet<>();


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        txWithCurrentTimeStamp = new Transaction();
        txWithCurrentTimeStamp.setEpoch(System.currentTimeMillis());
        txWithCurrentTimeStamp.setAmount(100);

        txWithExpiredTimeStamp = new Transaction();
    }

    @Test
    public void shouldAddNewKeyForTxWithUniqueTimeStamp() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Statistics stats = new Statistics(0,0);
        long txTime = txWithCurrentTimeStamp.getEpoch();

        when(appProps.getCalculateStatsForSec()).thenReturn(60);
        when(txManager.sortedByTimeMap.containsKey(txTime)).thenReturn(true);
        when(txManager.sortedByTimeMap.get(txTime)).thenReturn(stats);

        Method testMethod = incomingTnxConsumerThread.getClass().getDeclaredMethod("addToSortedTimeMap");
        testMethod.setAccessible(true);
        testMethod.invoke(txWithCurrentTimeStamp);

        Assert.assertEquals(stats.getCount(),1);
        Assert.assertEquals(stats.getSum(),txWithCurrentTimeStamp.getAmount());

    }

}
*/
