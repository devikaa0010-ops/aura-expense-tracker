package com.example.expense_tracker_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String category; // e.g., Food, Travel

    private String description;

    @Column(name = "payment_method")
    private String paymentMethod; // Cash, Card, UPI

    @Column(name = "transaction_type", nullable = false)
    private String type; // INCOME or EXPENSE

    @Column(name = "expense_type")
    private String expenseType; // FIXED or VARIABLE

    private String tags;

    private String location;

    @Column(name = "receipt_url")
    private String receiptUrl;
}
