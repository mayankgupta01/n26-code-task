package com.mayank.n26codetask.controllers;

import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;

import com.mayank.n26codetask.services.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

/**
 * Created by mayank.gupta on 10/06/17.
 */

@RestController
public class TransactionController {

    private final TransactionService txService;
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService service) {
        this.txService = service;
    }

    @Validated
    @RequestMapping(value = "/transaction", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<Void> transaction(@Valid @NotNull @RequestBody Transaction tx) {

        if(!validTransactionToProcess(tx))
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);

        return txService.insert(tx) ? new ResponseEntity<Void>(HttpStatus.CREATED) :
                new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<Statistics> getstatistics() {

        Statistics statistics = txService.getStatistics();
        return new ResponseEntity<>(statistics,HttpStatus.OK);
    }


    //      TODO: For some reason the javax.validation is not working. json keys and null check not working on requestbody, hence, doing manual validation below. Needs fixing
    private boolean validTransactionToProcess(Transaction tx) {
        if(tx == null)
            return false;

        if(tx.getEpoch() <= 0 || tx.getAmount() <= 0)
            return false;

        return true;
    }
}
