package com.example.expense_tracker_api.repository;

import com.example.expense_tracker_api.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);
    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(Long userId, String category, int month, int year);
}
