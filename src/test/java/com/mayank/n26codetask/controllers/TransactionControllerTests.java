
package com.mayank.n26codetask.controllers;

import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;
import com.mayank.n26codetask.services.TransactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionControllerTests {

    @Mock
    private TransactionService txService = mock(TransactionService.class);

    private TransactionController transactionController = new TransactionController(txService);

    private Transaction dummyTx;


    @Before
    public void setup() throws Exception{
        dummyTx = new Transaction();
        dummyTx.setEpoch(System.currentTimeMillis());
        dummyTx.setAmount(100);
    }

    @Test
    public void expect204ResponseForExpiredTransaction() {

        when(txService.insert(dummyTx)).thenReturn(false);

        ResponseEntity<Void> responseEntity;

        responseEntity = transactionController.transaction(dummyTx);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void expect201ResponseForCurrentTimeStampTransaction() {

        when(txService.insert(dummyTx)).thenReturn(true);

        ResponseEntity<Void> responseEntity;

        responseEntity = transactionController.transaction(dummyTx);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void expect400ResponseForNullTransaction() {

        ResponseEntity<Void> responseEntity;

        responseEntity = transactionController.transaction(null);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void expect400ResponseForTransactionWithZeroTimeStamp() {

        ResponseEntity<Void> responseEntity;

        dummyTx.setEpoch(0);
        responseEntity = transactionController.transaction(dummyTx);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void expect400ResponseForTransactionWithZeroAmount() {

        ResponseEntity<Void> responseEntity;

        dummyTx.setAmount(0);
        responseEntity = transactionController.transaction(dummyTx);

        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void expect200ResponseForGetStatistics() {

        Statistics stats = new Statistics.Builder(100,10).build();
        when(txService.getStatistics()).thenReturn(stats);

        ResponseEntity<Statistics> responseEntity;
        responseEntity = transactionController.getstatistics();

        Assert.assertEquals(responseEntity.getStatusCode(),HttpStatus.OK);
        Assert.assertEquals(responseEntity.hasBody(),true);
        Assert.assertEquals(responseEntity.getBody(),stats);

    }


}
