package com.example.expense_tracker_api.repository;

import com.example.expense_tracker_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndCategory(Long userId, String category);
    List<Transaction> findByUserIdAndType(Long userId, String type); // INCOME or EXPENSE
}
