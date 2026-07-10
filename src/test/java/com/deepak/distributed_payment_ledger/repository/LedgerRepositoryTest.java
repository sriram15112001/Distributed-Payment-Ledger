package com.deepak.distributed_payment_ledger.repository;

import com.deepak.distributed_payment_ledger.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class LedgerRepositoryTest {

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void setDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE accounts RESTART IDENTITY CASCADE");
    }

    @Test
    void testSave() {
        Account account = Account.builder()
                .ownerName("deepak")
                .currency("INR")
                .createdAt(LocalDateTime.now())
                .build();
        Account save = accountRepository.save(account);
        assertNotNull(save.getId());
    }

    @Test
    void testFetch() {
        Account account = Account.builder()
                .ownerName("deepak")
                .currency("INR")
                .createdAt(LocalDateTime.now())
                .build();
        accountRepository.save(account);
        Optional<Account> byId = accountRepository.findById(1L);
        Account acc = byId.orElse(null);
        assertEquals("deepak", acc.getOwnerName());
        assertEquals("INR", acc.getCurrency());
    }
}
