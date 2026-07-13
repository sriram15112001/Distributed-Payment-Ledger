package com.deepak.distributed_payment_ledger.exception;

import java.math.BigDecimal;

public class InvalidAmount extends RuntimeException{
    public InvalidAmount(BigDecimal amount) {
        super("Invalid amount : " + amount);
    }
}
