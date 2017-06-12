package com.mayank.n26codetask.entities;

import java.util.Comparator;

/**
 * Created by mayank.gupta on 10/06/17.
 */
public enum TransactionComparator implements Comparator<Transaction> {

    TIME {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            if(o1.getEpoch() < o2.getEpoch())
                return -1;
            if(o1.getEpoch() > o2.getEpoch())
                return 1;
            else
                return 0;
        }
    },

    AMOUNT {
        @Override
        public int compare(Transaction o1, Transaction o2) {

            if(o1.getAmount() == o2.getAmount()) {
                if(o1.getEpoch() < o2.getEpoch())
                    return -1;
                if(o1.getEpoch() > o2.getEpoch())
                    return 1;
                return 0;
            }

            if(o1.getAmount() < o2.getAmount())
                return -1;

            return 1;
        }
    }

}
