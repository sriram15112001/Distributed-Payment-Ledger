package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select coalesce(sum ( case when direction = 'DEBIT' then -amount else amount end ), 0) as amount from ledger_entry where account_id = :accountId", nativeQuery = true)
    BigDecimal accountBalance(Long accountId);

    boolean existsById(Long accountId);
}
