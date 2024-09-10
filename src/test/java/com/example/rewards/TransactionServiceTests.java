package com.example.rewards;

import com.example.rewards.model.Transaction;
import com.example.rewards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionServiceTests {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceTests.class);

    @Autowired
    private TransactionService transactionService;

    @Test
    void testCalculateRewardsWithNoTransactions() {
        try {
            logger.info("Testing rewards calculation with no transactions");
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            assertTrue(rewards.isEmpty());
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsWithNoTransactions: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsWithSingleTransaction() {
        try {
            logger.info("Testing rewards calculation with a single transaction");
            transactionService.saveTransaction(new Transaction("cust1", 150, LocalDate.of(2024, 1, 1)));
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            assertEquals(150, rewards.get("cust1").get("2024-01"));
            assertEquals(150, rewards.get("cust1").get("Total"));
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsWithSingleTransaction: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsWithBoundaryValues() {
        try {
            logger.info("Testing rewards calculation with boundary values");
            transactionService.saveTransaction(new Transaction("cust1", 100, LocalDate.of(2024, 1, 1)));
            transactionService.saveTransaction(new Transaction("cust1", 50, LocalDate.of(2024, 1, 2)));
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            assertEquals(50, rewards.get("cust1").get("2024-01"));
            assertEquals(50, rewards.get("cust1").get("Total"));
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsWithBoundaryValues: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsForMultipleCustomers() {
        try {
            logger.info("Testing rewards calculation for multiple customers");
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
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsForMultipleCustomers: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsForLowTransactions() {
        try {
            logger.info("Testing rewards calculation for low transactions");
            transactionService.saveTransaction(new Transaction("cust1", 30, LocalDate.of(2024, 1, 1)));
            transactionService.saveTransaction(new Transaction("cust1", 40, LocalDate.of(2024, 2, 2)));
            Map<String, Map<String, Integer>> rewards = transactionService.calculateRewardsPerMonthWithTotal();
            assertEquals(0, rewards.get("cust1").get("2024-01"));
            assertEquals(0, rewards.get("cust1").get("2024-02"));
            assertEquals(0, rewards.get("cust1").get("Total"));
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsForLowTransactions: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsWithMultipleTransactionsInOneDay() {
        try {
            logger.info("Testing rewards calculation with multiple transactions in one day");
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
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsWithMultipleTransactionsInOneDay: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testCalculateRewardsWithDifferentDates() {
        try {
            logger.info("Testing rewards calculation with different dates");
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
        } catch (Exception e) {
            logger.error("Error during testCalculateRewardsWithDifferentDates: {}", e.getMessage());
            throw e;
        }
    }
}
