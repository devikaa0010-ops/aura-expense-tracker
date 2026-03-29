package com.example.expense_tracker_api.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class MonthlySummary {
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance; // Income - Expense
    private Map<String, BigDecimal> expenseByCategory;
}
