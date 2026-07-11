package com.deepak.distributed_payment_ledger.controller;

import com.deepak.distributed_payment_ledger.dto.TransferRequest;
import com.deepak.distributed_payment_ledger.service.AccountService;
import com.deepak.distributed_payment_ledger.service.LedgerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {
    private final LedgerService ledgerService;
    private final AccountService accountService;

    @GetMapping("/health")
    public ResponseEntity<String> serviceUp() {
        return ResponseEntity.ok("up");
    }

    @PostMapping("/transactions")
    public ResponseEntity<Void> transaction(@RequestBody TransferRequest transferRequest) {
        ledgerService.transfer(transferRequest);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<BigDecimal> balance(@PathVariable(name = "accountId") Long accountId) {
        BigDecimal balance = accountService.getBalance(accountId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}
