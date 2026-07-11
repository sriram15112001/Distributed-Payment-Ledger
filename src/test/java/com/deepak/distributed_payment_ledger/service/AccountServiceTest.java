package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    public AccountService ledgerService;

    @Mock
    public AccountRepository accountRepository;


    @Test
    public void testSave() {
        Account act = Account.builder()
                .ownerName("deepak")
                .createdAt(LocalDateTime.now())
                .currency("INR")
                .id(1L)
                .build();
        when(accountRepository.save(any(Account.class))).thenReturn(act);
        Long l = ledgerService.saveAccount("deepak", "INR");
        assertEquals(1L, l);
    }

}
