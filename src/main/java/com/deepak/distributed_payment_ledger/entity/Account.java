package com.deepak.distributed_payment_ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "accounts")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    @Column(name = "currency", nullable = false)
    private String currency;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void createdOn() {
        this.createdAt = LocalDateTime.now();
    }
}
