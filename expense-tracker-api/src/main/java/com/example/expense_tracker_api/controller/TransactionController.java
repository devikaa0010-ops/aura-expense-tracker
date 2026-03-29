package com.example.expense_tracker_api.controller;

import com.example.expense_tracker_api.entity.Transaction;
import com.example.expense_tracker_api.entity.User;
import com.example.expense_tracker_api.repository.TransactionRepository;
import com.example.expense_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        User user = getAuthenticatedUser();
        return transactionRepository.findByUserId(user.getId());
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        User user = getAuthenticatedUser();
        transaction.setUser(user);
        Transaction saved = transactionRepository.save(transaction);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            User user = getAuthenticatedUser();
            if(!transaction.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Error: Unauthorized to access this transaction");
            }
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            User user = getAuthenticatedUser();
            if(!transaction.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Error: Unauthorized to access this transaction");
            }
            transactionRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
