package io.github.lekko8.financetracker.cli;

import io.github.lekko8.financetracker.core.exception.ValidationException;
import io.github.lekko8.financetracker.core.model.User;
import io.github.lekko8.financetracker.core.model.Wallet;
import io.github.lekko8.financetracker.core.repository.WalletRepository;

public class Session {

    private User user;
    private Wallet wallet;
    private boolean running = true;

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void login(User user, Wallet wallet) {
        this.user = user;
        this.wallet = wallet;
    }

    public Wallet openWallet(User user, WalletRepository wallets) {
        Wallet wallet = wallets.findByOwner(user.login())
                .orElseGet(() -> new Wallet(user.login()));
        login(user, wallet);
        return wallet;
    }

    public void logout() {
        user = null;
        wallet = null;
    }

    public User user() {
        requireLoggedIn();
        return user;
    }

    public Wallet wallet() {
        requireLoggedIn();
        return wallet;
    }

    public void requireLoggedOut() {
        if (user != null) {
            throw new ValidationException(
                    "Вы уже вошли как " + user.login() + ". Сначала выполните logout.");
        }
    }

    private void requireLoggedIn() {
        if (user == null) {
            throw new ValidationException("Сначала войдите в систему: login или register.");
        }
    }
}