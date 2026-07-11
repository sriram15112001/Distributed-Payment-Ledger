package com.deepak.distributed_payment_ledger.enums;

public enum LedgerDirection {
    CREDIT("Credit"),
    DEBIT("Debit");

    public final String direction;

    LedgerDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return this.direction;
    }
}
