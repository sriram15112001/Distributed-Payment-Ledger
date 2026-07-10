package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LedgerService {
    private final AccountRepository accountRepository;

    public Long saveAccount(String ownerName, String currency) {
        Account account = Account.builder()
                .ownerName(ownerName)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .build();
        Account save = accountRepository.save(account);
        return save.getId();
    }
}
