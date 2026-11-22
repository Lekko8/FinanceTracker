package io.github.lekko8.financetracker.core.model;

import java.math.BigDecimal;

public record Budget(String category, BigDecimal limit) {

    public Budget {
        category = new Category(category).name();
        limit = Money.positive(limit);
    }
}
