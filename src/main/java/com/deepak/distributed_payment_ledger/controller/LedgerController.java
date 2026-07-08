package com.deepak.distributed_payment_ledger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    @GetMapping("/health")
    public ResponseEntity<String> serviceUp() {
        return ResponseEntity.ok("up");
    }
}
