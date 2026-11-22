package io.github.lekko8.financetracker.core.service;

import io.github.lekko8.financetracker.core.model.Budget;
import io.github.lekko8.financetracker.core.model.Wallet;
import java.math.BigDecimal;

public class BudgetService {

    public Budget setBudget(Wallet wallet, String category, BigDecimal limit) {
        Budget budget = new Budget(category, limit);
        wallet.setBudget(budget);
        return budget;
    }

    public void removeBudget(Wallet wallet, String category) {
        wallet.removeBudget(category);
    }
}
