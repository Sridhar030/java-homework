package com.example.rewards.service;

import com.example.rewards.model.Transaction;
import com.example.rewards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public void saveTransaction(Transaction transaction) {
        repository.save(transaction);
    }

    public Map<String, Map<String, Integer>> calculateRewardsPerMonthWithTotal() {
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
