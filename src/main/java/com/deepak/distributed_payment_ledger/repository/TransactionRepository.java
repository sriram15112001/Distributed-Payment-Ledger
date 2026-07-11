package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
