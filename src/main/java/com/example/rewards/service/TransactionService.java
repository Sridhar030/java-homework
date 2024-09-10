package com.example.rewards.service;

import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository repository;

    public void saveTransaction(Transaction transaction) {
        try {
            logger.info("Saving transaction: {}", transaction);
            repository.save(transaction);
        } catch (Exception e) {
            logger.error("Error saving transaction: {}", e.getMessage());
            throw new RuntimeException("Failed to save transaction");
        }
    }

    public Map<String, Map<String, Integer>> calculateRewardsPerMonthWithTotal() {
        try {
            logger.info("Calculating rewards for all transactions");
            List<Transaction> transactions = repository.findAll();
            return transactions.stream()
                    .collect(Collectors.groupingBy(
                            Transaction::getCustomerId,
                            Collectors.collectingAndThen(
                                    Collectors.groupingBy(
                                            transaction -> YearMonth.from(transaction.getDate()).toString(),
                                            Collectors.summingInt(this::calculatePoints)
                                    ),
                                    monthlyRewards -> {
                                        int totalRewards = monthlyRewards.values().stream()
                                                .mapToInt(Integer::intValue)
                                                .sum();
                                        monthlyRewards.put("Total", totalRewards);
                                        return monthlyRewards;
                                    }
                            )
                    ));
        } catch (Exception e) {
            logger.error("Error calculating rewards: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate rewards");
        }
    }

    private int calculatePoints(Transaction transaction) {
        int amount = transaction.getAmount();
        int points = 0;
        if (amount > 100) {
            points += (amount - 100) * 2;
            amount = 100;
        }
        if (amount > 50) {
            points += (amount - 50);
        }
        return points;
    }
}
