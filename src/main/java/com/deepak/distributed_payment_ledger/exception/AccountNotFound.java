package com.deepak.distributed_payment_ledger.exception;

public class AccountNotFound extends RuntimeException{
    public AccountNotFound(Long accountId) {
        super("Account Number : " + accountId + " not found");
    }
}
