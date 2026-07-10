package com.deepak.distributed_payment_ledger.enums;

public enum TransactionEnum {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String status;

     TransactionEnum(String status) {
        this.status = status;
    }

    String getStatus() {
         return this.status;
    }

}
