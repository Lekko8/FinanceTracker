package io.github.lekko8.financetracker.core.repository;

import io.github.lekko8.financetracker.core.model.Wallet;
import java.util.Optional;

public interface WalletRepository {

    Optional<Wallet> findByOwner(String login);

    void save(Wallet wallet);
}
