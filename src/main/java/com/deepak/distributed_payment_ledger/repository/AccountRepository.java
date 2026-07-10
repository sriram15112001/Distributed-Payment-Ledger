package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
