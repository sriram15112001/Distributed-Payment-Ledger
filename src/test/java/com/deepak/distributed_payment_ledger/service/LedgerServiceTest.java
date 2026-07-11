package com.deepak.distributed_payment_ledger.service;

import com.deepak.distributed_payment_ledger.dto.TransferRequest;
import com.deepak.distributed_payment_ledger.entity.Account;
import com.deepak.distributed_payment_ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class LedgerServiceTest {
    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void setDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE accounts, transactions, ledger_entry RESTART IDENTITY CASCADE");
    }

    @Test
    public void transferTest() {
        Account alice = accountRepository.save(Account.builder()
                .ownerName("alice").currency("INR")
                .build());
        Account bob = accountRepository.save(Account.builder()
                .ownerName("bob").currency("INR")
                .build());

        TransferRequest request = new TransferRequest(alice.getId(), bob.getId(), "INR", BigDecimal.valueOf(100));

        boolean transfer = ledgerService.transfer(request);
        assertTrue(transfer);

        assertEquals(0, new BigDecimal("-100.0000").compareTo(accountRepository.accountBalance(alice.getId())));
        assertEquals(0, new BigDecimal("100.0000").compareTo(accountRepository.accountBalance(bob.getId())));


    }
}
