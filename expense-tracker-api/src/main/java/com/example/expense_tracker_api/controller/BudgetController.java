package com.example.expense_tracker_api.controller;

import com.example.expense_tracker_api.entity.Budget;
import com.example.expense_tracker_api.entity.User;
import com.example.expense_tracker_api.repository.BudgetRepository;
import com.example.expense_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

    @GetMapping("/{year}/{month}")
    public List<Budget> getBudgetsForMonth(@PathVariable int year, @PathVariable int month) {
        User user = getAuthenticatedUser();
        return budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateBudget(@RequestBody Budget budget) {
        User user = getAuthenticatedUser();
        // Check if a budget already exists for this category, month, and year
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                user.getId(), budget.getCategory(), budget.getMonth(), budget.getYear());

        if (existing.isPresent()) {
            Budget b = existing.get();
            b.setLimitAmount(budget.getLimitAmount());
            return ResponseEntity.ok(budgetRepository.save(b));
        }

        budget.setUser(user);
        return ResponseEntity.ok(budgetRepository.save(budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        Optional<Budget> budget = budgetRepository.findById(id);

        if (budget.isPresent()) {
            User user = getAuthenticatedUser();
            if(!budget.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Error: Unauthorized to access this budget");
            }
            budgetRepository.deleteById(id);
            return ResponseEntity.ok("Budget deleted successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
