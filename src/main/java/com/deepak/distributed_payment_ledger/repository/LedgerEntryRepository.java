package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}
