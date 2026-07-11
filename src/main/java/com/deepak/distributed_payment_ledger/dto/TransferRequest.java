package com.deepak.distributed_payment_ledger.dto;

import java.math.BigDecimal;

public record TransferRequest(Long fromAccountId, Long toAccountId, String currency, BigDecimal amount) { }
