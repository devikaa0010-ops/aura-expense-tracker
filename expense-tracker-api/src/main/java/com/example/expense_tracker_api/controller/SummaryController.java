package com.example.expense_tracker_api.controller;

import com.example.expense_tracker_api.dto.MonthlySummary;
import com.example.expense_tracker_api.entity.Transaction;
import com.example.expense_tracker_api.entity.User;
import com.example.expense_tracker_api.repository.TransactionRepository;
import com.example.expense_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<MonthlySummary> getMonthlySummary(@PathVariable int year, @PathVariable int month) {
        User user = getAuthenticatedUser();
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                user.getId(), startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if ("INCOME".equalsIgnoreCase(t.getType())) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                totalExpense = totalExpense.add(t.getAmount());
                
                String cat = t.getCategory();
                if(cat == null) cat = "Uncategorized";
                
                expenseByCategory.put(cat, expenseByCategory.getOrDefault(cat, BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        MonthlySummary summary = new MonthlySummary(
            month, year, totalIncome, totalExpense, balance, expenseByCategory
        );

        return ResponseEntity.ok(summary);
    }
}
