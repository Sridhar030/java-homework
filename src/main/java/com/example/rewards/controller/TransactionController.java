package com.example.rewards.controller;

import com.example.rewards.service.TransactionService;
import com.example.rewards.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction) {
        try {
            logger.info("Received transaction for saving: {}", transaction);
            transactionService.saveTransaction(transaction);
            return ResponseEntity.ok("Transaction saved successfully");
        } catch (RuntimeException e) {
            logger.error("Error adding transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding transaction: " + e.getMessage());
        }
    }

    @GetMapping("/rewards-per-month-with-total")
    public ResponseEntity<Map<String, Map<String, Integer>>> getRewardsPerMonthWithTotal() {
        try {
            logger.info("Getting rewards per month with total");
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            return ResponseEntity.ok(rewards);
        } catch (RuntimeException e) {
            logger.error("Error retrieving rewards: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
