package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.dto.TransferRequest;
import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.entity.LedgerEntry;
import com.deepak.distributed_payment_ledger.entity.Transaction;
import com.deepak.distributed_payment_ledger.enums.LedgerDirection;
import com.deepak.distributed_payment_ledger.enums.TransactionEnum;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import com.deepak.distributed_payment_ledger.repository.LedgerEntryRepository;
import com.deepak.distributed_payment_ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public boolean transfer(TransferRequest transferRequest) {
        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .status(TransactionEnum.PENDING)
                        .build()
        );

        Optional<Account> fromAccount = accountRepository.findById(transferRequest.fromAccountId());
        Optional<Account> toAccount = accountRepository.findById(transferRequest.toAccountId());

        if (fromAccount.isEmpty() || toAccount.isEmpty()) {
            transaction.setStatus(TransactionEnum.FAILED);
            transactionRepository.save(transaction);
            return false;
        }

        LedgerEntry ledgerFromAccount = LedgerEntry.builder()
                .accountId(fromAccount.get())
                .transactionId(transaction)
                .direction(LedgerDirection.DEBIT)
                .currency(transferRequest.currency())
                .amount(transferRequest.amount())
                .build();

        LedgerEntry ledgerToAccount = LedgerEntry.builder()
                .accountId(toAccount.get())
                .transactionId(transaction)
                .direction(LedgerDirection.CREDIT)
                .currency(transferRequest.currency())
                .amount(transferRequest.amount())
                .build();

        ledgerEntryRepository.saveAll(List.of(ledgerToAccount, ledgerFromAccount));

        transaction.setStatus(TransactionEnum.COMPLETED);

        transactionRepository.save(transaction);
        return true;

    }

}
