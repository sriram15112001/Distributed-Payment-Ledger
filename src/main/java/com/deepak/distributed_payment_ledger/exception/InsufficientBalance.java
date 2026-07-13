package com.deepak.distributed_payment_ledger.exception;

public class InsufficientBalance extends RuntimeException{
    public InsufficientBalance(Long accountId) {
        super("Insufficient Balance in account " + accountId);
    }
}
