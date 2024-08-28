package com.example.rewards;

import com.example.rewards.model.Transaction;
import com.example.rewards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        assertTrue(rewards.isEmpty());
    }

    @Test
    void testCalculateRewardsWithSingleTransaction() {
        transactionService.saveTransaction(new Transaction("cust1", 150, LocalDate.of(2024, 1, 1)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        assertEquals(150, rewards.get("cust1").get("2024-01"));
        assertEquals(150, rewards.get("cust1").get("Total"));
    }

    @Test
    void testCalculateRewardsWithBoundaryValues() {
        transactionService.saveTransaction(new Transaction("cust1", 100, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 50, LocalDate.of(2024, 1, 2)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        assertEquals(50, rewards.get("cust1").get("2024-01"));
        assertEquals(50, rewards.get("cust1").get("Total"));
    }

    @Test
    void testCalculateRewardsForMultipleCustomers() {
        transactionService.saveTransaction(new Transaction("cust1", 150, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust2", 70, LocalDate.of(2024, 1, 2)));
        transactionService.saveTransaction(new Transaction("cust1", 200, LocalDate.of(2024, 2, 10)));
        transactionService.saveTransaction(new Transaction("cust2", 90, LocalDate.of(2024, 3, 5)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        // Customer 1 - January
        assertEquals(150, rewards.get("cust1").get("2024-01"));
        // Customer 1 - February
        assertEquals(250, rewards.get("cust1").get("2024-02"));
        // Customer 1 - Total
        assertEquals(400, rewards.get("cust1").get("Total"));

        // Customer 2 - January
        assertEquals(20, rewards.get("cust2").get("2024-01"));
        // Customer 2 - March
        assertEquals(40, rewards.get("cust2").get("2024-03"));
        // Customer 2 - Total
        assertEquals(60, rewards.get("cust2").get("Total"));
    }

    @Test
    void testCalculateRewardsForLowTransactions() {
        transactionService.saveTransaction(new Transaction("cust1", 30, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 40, LocalDate.of(2024, 2, 2)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        assertEquals(0, rewards.get("cust1").get("2024-01"));
        assertEquals(0, rewards.get("cust1").get("2024-02"));
        assertEquals(0, rewards.get("cust1").get("Total"));
    }

    @Test
    void testCalculateRewardsWithMultipleTransactionsInOneDay() {
        transactionService.saveTransaction(new Transaction("cust1", 60, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 120, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 70, LocalDate.of(2024, 2, 15)));
        transactionService.saveTransaction(new Transaction("cust1", 150, LocalDate.of(2024, 2, 15)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        // Customer 1 - January
        assertEquals(100, rewards.get("cust1").get("2024-01"));
        // Customer 1 - February
        assertEquals(170, rewards.get("cust1").get("2024-02"));
        // Customer 1 - Total
        assertEquals(270, rewards.get("cust1").get("Total"));
    }

    @Test
    void testCalculateRewardsWithDifferentDates() {
        transactionService.saveTransaction(new Transaction("cust1", 120, LocalDate.of(2024, 1, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 80, LocalDate.of(2024, 1, 2)));
        transactionService.saveTransaction(new Transaction("cust1", 130, LocalDate.of(2024, 2, 1)));
        transactionService.saveTransaction(new Transaction("cust1", 50, LocalDate.of(2024, 2, 2)));
        transactionService.saveTransaction(new Transaction("cust2", 200, LocalDate.of(2024, 2, 1)));
        transactionService.saveTransaction(new Transaction("cust2", 30, LocalDate.of(2024, 3, 1)));

        Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();

        // Customer 1 - January
        assertEquals(120, rewards.get("cust1").get("2024-01"));
        // Customer 1 - February
        assertEquals(110, rewards.get("cust1").get("2024-02"));
        // Customer 1 - Total
        assertEquals(230, rewards.get("cust1").get("Total"));

        // Customer 2 - February
        assertEquals(250, rewards.get("cust2").get("2024-02"));
        // Customer 2 - March
        assertEquals(0, rewards.get("cust2").get("2024-03"));
        // Customer 2 - Total
        assertEquals(250, rewards.get("cust2").get("Total"));
    }
}
