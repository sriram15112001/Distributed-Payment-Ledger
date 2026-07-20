package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select coalesce(sum ( case when direction = 'DEBIT' then -amount else amount end ), 0) as amount from ledger_entry where account_id = :accountId", nativeQuery = true)
    BigDecimal accountBalance(Long accountId);

    boolean existsById(Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a from Account a where a.id = :accountId")
    Optional<Account> findByIdForUpdate(@Param(value = "accountId") Long accountId);
}
