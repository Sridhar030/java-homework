package com.example.rewards.controller;

import com.example.rewards.service.TransactionService;
import com.example.rewards.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Adds a new transaction and saves it in the system.
     *
     * @param transaction The transaction object to be saved.
     * @return ResponseEntity with success or error message.
     */
    @Operation(summary = "Add a new transaction", description = "This endpoint allows adding a new transaction to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction saved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
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

    /**
     * Retrieves rewards per month and the total rewards for all customers.
     *
     * @return ResponseEntity with a map of rewards per month and total rewards for each customer.
     */
    @Operation(summary = "Get rewards per month with total", description = "This endpoint retrieves the rewards per month for each customer, including the total.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rewards retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/rewards-per-month-with-total")
    public ResponseEntity<Map<String, Map<String, Integer>>> getRewardsPerMonthWithTotal() {
        try {
            logger.info("Getting rewards per month with total");
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            return ResponseEntity.ok(rewards);
        } catch (RuntimeException e) {
            logger.error("Error retrieving rewards: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
