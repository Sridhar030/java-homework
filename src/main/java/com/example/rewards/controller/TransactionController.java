package com.example.rewards.controller;

import com.example.rewards.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rewards.model.Transaction;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public void addTransaction(@RequestBody Transaction transaction) {
        transactionService.saveTransaction(transaction);
    }

    @GetMapping("/rewards-per-month-with-total")
    public Map<String, Map<String, Integer>> getRewardsPerMonthWithTotal() {
        return transactionService.calculateRewardsPerMonthWithTotal();
    }
}
