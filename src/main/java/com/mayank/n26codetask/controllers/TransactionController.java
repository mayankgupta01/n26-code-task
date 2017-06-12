package com.mayank.n26codetask.controllers;

import com.mayank.n26codetask.entities.Statistics;
import com.mayank.n26codetask.entities.Transaction;

import com.mayank.n26codetask.services.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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


    @RequestMapping(value = "/transaction", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> transaction(@Valid @RequestBody Transaction tx) {

        boolean validTx = txService.insert(tx);
        return validTx ? new ResponseEntity<Void>(HttpStatus.CREATED) : new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<Statistics> getstatistics() {

        Statistics statistics = txService.getStatistics();
        return new ResponseEntity<>(statistics,HttpStatus.OK);
    }
}
