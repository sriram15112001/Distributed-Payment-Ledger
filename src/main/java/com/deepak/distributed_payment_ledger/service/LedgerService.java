package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.dto.TransferRequest;
import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.entity.LedgerEntry;
import com.deepak.distributed_payment_ledger.entity.Transaction;
import com.deepak.distributed_payment_ledger.enums.LedgerDirection;
import com.deepak.distributed_payment_ledger.enums.TransactionEnum;
import com.deepak.distributed_payment_ledger.exception.AccountNotFound;
import com.deepak.distributed_payment_ledger.exception.InsufficientBalance;
import com.deepak.distributed_payment_ledger.exception.InvalidAmount;
import com.deepak.distributed_payment_ledger.exception.SameAccountTransaction;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import com.deepak.distributed_payment_ledger.repository.LedgerEntryRepository;
import com.deepak.distributed_payment_ledger.repository.TransactionRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public void transfer(TransferRequest transferRequest) {
        if (transferRequest.amount().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidAmount(transferRequest.amount());
        if (transferRequest.fromAccountId().equals(transferRequest.toAccountId())) throw new SameAccountTransaction(transferRequest.fromAccountId());

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .status(TransactionEnum.PENDING)
                        .build()
        );

        Optional<Account> fromAccount = accountRepository.findByIdForUpdate(transferRequest.fromAccountId());
        Optional<Account> toAccount = accountRepository.findByIdForUpdate(transferRequest.toAccountId());

        if (fromAccount.isEmpty() || toAccount.isEmpty()) {
            if (fromAccount.isEmpty()) throw new AccountNotFound(transferRequest.fromAccountId());
            throw new AccountNotFound(transferRequest.toAccountId());
        }

        Account senderAccount = fromAccount.get();
        Account receiverAccount = toAccount.get();

        if (accountRepository.accountBalance(senderAccount.getId()).compareTo(transferRequest.amount()) < 0) throw new InsufficientBalance(senderAccount.getId());

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
    }

}
