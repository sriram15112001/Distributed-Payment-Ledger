package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.dto.TransferRequest;
import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.exception.AccountNotFound;
import com.deepak.distributed_payment_ledger.exception.InsufficientBalance;
import com.deepak.distributed_payment_ledger.exception.InvalidAmount;
import com.deepak.distributed_payment_ledger.exception.SameAccountTransaction;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class LedgerServiceTest {

    @Container
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void setDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Account alice;
    private Account bob;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE ledger_entry, transactions, accounts RESTART IDENTITY CASCADE");

        alice = accountRepository.save(
                Account.builder().ownerName("Alice").currency("INR").createdAt(LocalDateTime.now()).build());
        bob = accountRepository.save(
                Account.builder().ownerName("Bob").currency("INR").createdAt(LocalDateTime.now()).build());

        // give Alice some starting balance to transfer from, in later tests that need it
        jdbcTemplate.update("INSERT INTO transactions (status, created_at) VALUES (?, ?)",
                "COMPLETED", LocalDateTime.now());
        jdbcTemplate.update("""
                INSERT INTO ledger_entry (transaction_id, account_id, direction, amount, currency, created_at)
                VALUES (1, ?, 'CREDIT', 200.00, 'INR', ?)
                """, alice.getId(), LocalDateTime.now());
    }

    @Test
    void transferMovesMoneyCorrectlyBetweenAccounts() {
        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(),"INR", new BigDecimal("100.00"));

        ledgerService.transfer(request);

        BigDecimal aliceBalance = accountRepository.accountBalance(alice.getId());
        BigDecimal bobBalance = accountRepository.accountBalance(bob.getId());

        assertEquals(0, aliceBalance.compareTo(new BigDecimal("100.0000"))); // 200 - 100
        assertEquals(0, bobBalance.compareTo(new BigDecimal("100.0000")));
    }

    @Test
    void transferThrowsOnZeroAmount() {
        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(), "INR", BigDecimal.ZERO);

        assertThrows(InvalidAmount.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferThrowsOnNegativeAmount() {
        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(),  "INR", new BigDecimal("-50.00"));

        assertThrows(InvalidAmount.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferThrowsWhenSameAccount() {
        TransferRequest request = new TransferRequest(alice.getId(), alice.getId(), "INR", new BigDecimal("50.00"));

        assertThrows(SameAccountTransaction.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferThrowsWhenFromAccountDoesNotExist() {
        TransferRequest request = new TransferRequest(9999L, bob.getId(), "INR", new BigDecimal("50.00"));

        assertThrows(AccountNotFound.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferThrowsWhenToAccountDoesNotExist() {
        TransferRequest request = new TransferRequest(alice.getId(), 9999L,"INR", new BigDecimal("50.00"));

        assertThrows(AccountNotFound.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferThrowsWhenInsufficientBalance() {
        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(), "INR",new BigDecimal("500.00"));

        assertThrows(InsufficientBalance.class, () -> ledgerService.transfer(request));
    }

    @Test
    void transferRollsBackCompletelyWhenAccountNotFound() {
        Long transactionCountBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions", Long.class);
        Long ledgerEntryCountBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ledger_entry", Long.class);

        TransferRequest request = new TransferRequest(alice.getId(), 9999L, "INR", new BigDecimal("50.00"));

        assertThrows(AccountNotFound.class, () -> ledgerService.transfer(request));

        Long transactionCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions", Long.class);
        Long ledgerEntryCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ledger_entry", Long.class);

        assertEquals(transactionCountBefore, transactionCountAfter);
        assertEquals(ledgerEntryCountBefore, ledgerEntryCountAfter);
    }

    @Test
    void transferRollsBackCompletelyWhenInsufficientBalance() {
        Long transactionCountBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions", Long.class);

        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(), "INR", new BigDecimal("999.00"));

        assertThrows(InsufficientBalance.class, () -> ledgerService.transfer(request));

        Long transactionCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions", Long.class);

        assertEquals(transactionCountBefore, transactionCountAfter);
    }

    @Test
    void concurrentTransactionsTest() throws InterruptedException {
        int times = 20;
        ExecutorService service = Executors.newFixedThreadPool(times);
        CountDownLatch countDownLatch = new CountDownLatch(times);
        TransferRequest tr = new TransferRequest(1L, 2L, "INR", BigDecimal.valueOf(10));
        for(int i = 0; i < times; i++) {
            service.submit(() -> {
                ledgerService.transfer(tr);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        BigDecimal aliceBalance = accountRepository.accountBalance(1L);
        BigDecimal bobBalance = accountRepository.accountBalance(2L);

        assertEquals(new BigDecimal("0.0000"), aliceBalance);
        assertEquals(new BigDecimal("200.0000"), bobBalance);

    }
}