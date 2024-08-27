package com.example.rewards;

import com.example.rewards.model.Transaction;
import com.example.rewards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionServiceTests {

    @Autowired
    private TransactionService transactionService;

    @Test
    void testCalculateRewardsWithNoTransactions() {
        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertTrue(rewards.isEmpty());
    }

    @Test
    void testCalculateRewardsWithSingleTransaction() {
        transactionService.saveTransaction(new Transaction("cust1", 150, "2024-01-01"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(150, rewards.get("cust1"));
    }

    @Test
    void testCalculateRewardsWithBoundaryValues() {
        transactionService.saveTransaction(new Transaction("cust1", 100, "2024-01-01"));
        transactionService.saveTransaction(new Transaction("cust1", 50, "2024-01-02"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(50, rewards.get("cust1"));
    }

    @Test
    void testCalculateRewardsForMultipleCustomers() {
        transactionService.saveTransaction(new Transaction("cust1", 150, "2024-01-01"));
        transactionService.saveTransaction(new Transaction("cust2", 70, "2024-01-02"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(150, rewards.get("cust1"));
        assertEquals(20, rewards.get("cust2"));
    }

    @Test
    void testCalculateRewardsForLowTransactions() {
        transactionService.saveTransaction(new Transaction("cust1", 30, "2024-01-01"));
        transactionService.saveTransaction(new Transaction("cust1", 40, "2024-01-02"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(0, rewards.get("cust1"));
    }

    @Test
    void testCalculateRewardsWithMultipleTransactionsInOneDay() {
        transactionService.saveTransaction(new Transaction("cust1", 60, "2024-01-01"));
        transactionService.saveTransaction(new Transaction("cust1", 120, "2024-01-01"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(100, rewards.get("cust1"));
    }

    @Test
    void testCalculateRewardsWithDifferentDates() {
        transactionService.saveTransaction(new Transaction("cust1", 120, "2024-01-01"));
        transactionService.saveTransaction(new Transaction("cust1", 80, "2024-01-02"));
        transactionService.saveTransaction(new Transaction("cust2", 200, "2024-02-01"));
        transactionService.saveTransaction(new Transaction("cust2", 30, "2024-03-01"));

        Map<String, Integer> rewards = transactionService.calculateRewards();

        assertEquals(120, rewards.get("cust1"));
        assertEquals(250, rewards.get("cust2"));
    }
}
