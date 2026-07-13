package com.deepak.distributed_payment_ledger.exception;

public class SameAccountTransaction extends RuntimeException {
    public SameAccountTransaction(Long accountId) {
        super("Same account Transaction for account number : " + accountId);
    }
}
