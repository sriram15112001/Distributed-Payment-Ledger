package com.deepak.distributed_payment_ledger.entity;

import com.deepak.distributed_payment_ledger.enums.TransactionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long transactionId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void createdOn() {
        this.createdAt = LocalDateTime.now();
    }
}
