package io.github.lekko8.financetracker.cli;

import io.github.lekko8.financetracker.core.model.User;
import io.github.lekko8.financetracker.core.model.Wallet;
import io.github.lekko8.financetracker.core.repository.WalletRepository;
import io.github.lekko8.financetracker.core.service.AuthService;
import io.github.lekko8.financetracker.core.service.NotificationService;
import java.util.Set;

public class LoginCommand implements Command {

    private final AuthService auth;
    private final WalletRepository wallets;
    private final NotificationService notifications;

    public LoginCommand(
            AuthService auth, WalletRepository wallets, NotificationService notifications) {
        this.auth = auth;
        this.wallets = wallets;
        this.notifications = notifications;
    }

    @Override
    public String name() {
        return "login";
    }

    @Override
    public String usage() {
        return "login";
    }

    @Override
    public String description() {
        return "Войти по логину и паролю";
    }

    @Override
    public String example() {
        return "login";
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public void execute(CommandLine line, Session session, Terminal terminal) {
        line.check(0, 0, Set.of(), usage());
        session.requireLoggedOut();
        String login = terminal.ask("Логин: ");
        String password = terminal.ask("Пароль: ");
        User user = auth.login(login, password);
        Wallet wallet = session.openWallet(user, wallets);
        terminal.println("Добро пожаловать, " + user.login() + "! Баланс: "
                + MoneyFormatter.format(wallet.getBalance()) + ".");
        if (wallet.getCategories().isEmpty()) {
            terminal.println("Кошелёк пока пуст. Начните с категорий: add-category Еда.");
        }
        NotificationPrinter.show(terminal, notifications.currentState(wallet));
    }
}
