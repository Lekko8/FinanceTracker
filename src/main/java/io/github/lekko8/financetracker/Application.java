package io.github.lekko8.financetracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lekko8.financetracker.cli.AddCategoryCommand;
import io.github.lekko8.financetracker.cli.AddOperationCommand;
import io.github.lekko8.financetracker.cli.CategoriesCommand;
import io.github.lekko8.financetracker.cli.CommandLoop;
import io.github.lekko8.financetracker.cli.CommandRegistry;
import io.github.lekko8.financetracker.cli.ExitCommand;
import io.github.lekko8.financetracker.cli.HelpCommand;
import io.github.lekko8.financetracker.cli.HistoryCommand;
import io.github.lekko8.financetracker.cli.LoginCommand;
import io.github.lekko8.financetracker.cli.LogoutCommand;
import io.github.lekko8.financetracker.cli.RegisterCommand;
import io.github.lekko8.financetracker.cli.RemoveBudgetCommand;
import io.github.lekko8.financetracker.cli.RemoveCategoryCommand;
import io.github.lekko8.financetracker.cli.RenameCategoryCommand;
import io.github.lekko8.financetracker.cli.ReportCommand;
import io.github.lekko8.financetracker.cli.Session;
import io.github.lekko8.financetracker.cli.SetBudgetCommand;
import io.github.lekko8.financetracker.cli.Terminal;
import io.github.lekko8.financetracker.cli.TransferCommand;
import io.github.lekko8.financetracker.core.model.OperationType;
import io.github.lekko8.financetracker.core.repository.UserRepository;
import io.github.lekko8.financetracker.core.repository.WalletRepository;
import io.github.lekko8.financetracker.core.service.AuthService;
import io.github.lekko8.financetracker.core.service.BudgetService;
import io.github.lekko8.financetracker.core.service.CategoryService;
import io.github.lekko8.financetracker.core.service.NotificationService;
import io.github.lekko8.financetracker.core.service.OperationService;
import io.github.lekko8.financetracker.core.service.StatisticsService;
import io.github.lekko8.financetracker.core.service.TransferService;
import io.github.lekko8.financetracker.infra.Pbkdf2PasswordHasher;
import io.github.lekko8.financetracker.infra.json.JsonMapperFactory;
import io.github.lekko8.financetracker.infra.json.JsonUserRepository;
import io.github.lekko8.financetracker.infra.json.JsonWalletRepository;
import io.github.lekko8.financetracker.cli.ExportCommand;
import io.github.lekko8.financetracker.cli.ImportCommand;
import io.github.lekko8.financetracker.core.service.FormatRegistry;
import io.github.lekko8.financetracker.core.service.ImportService;
import io.github.lekko8.financetracker.infra.csv.CsvWalletFormat;
import io.github.lekko8.financetracker.infra.json.JsonWalletFormat;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class Application {

    private final Path dataDir;
    private final int passwordIterations;

    public Application(Path dataDir, int passwordIterations) {
        this.dataDir = dataDir;
        this.passwordIterations = passwordIterations;
    }

    public void run(InputStream in, OutputStream out) {
        ObjectMapper mapper = JsonMapperFactory.create();
        UserRepository users = new JsonUserRepository(dataDir.resolve("users.json"), mapper);
        WalletRepository wallets = new JsonWalletRepository(dataDir.resolve("wallets"), mapper);

        StatisticsService statistics = new StatisticsService();
        NotificationService notifications = new NotificationService(statistics);
        AuthService auth = new AuthService(users, new Pbkdf2PasswordHasher(passwordIterations));
        CategoryService categories = new CategoryService();
        BudgetService budgets = new BudgetService();
        OperationService operations = new OperationService();
        TransferService transfers = new TransferService(users, wallets);
        FormatRegistry formats =
                new FormatRegistry(List.of(new CsvWalletFormat(), new JsonWalletFormat(mapper)));
        ImportService importService = new ImportService();

        CommandRegistry registry = new CommandRegistry();
        registry.register(new HelpCommand(registry));
        registry.register(new RegisterCommand(auth, wallets));
        registry.register(new LoginCommand(auth, wallets, notifications));
        registry.register(new LogoutCommand(wallets));
        registry.register(new CategoriesCommand());
        registry.register(new AddCategoryCommand(categories));
        registry.register(new RenameCategoryCommand(categories));
        registry.register(new RemoveCategoryCommand(categories));
        registry.register(new SetBudgetCommand(budgets));
        registry.register(new RemoveBudgetCommand(budgets));
        registry.register(new AddOperationCommand(OperationType.INCOME, operations, notifications));
        registry.register(new AddOperationCommand(OperationType.EXPENSE, operations, notifications));
        registry.register(new TransferCommand(transfers, notifications));
        registry.register(new ReportCommand(statistics, notifications));
        registry.register(new HistoryCommand(statistics));
        registry.register(new ExportCommand(formats));
        registry.register(new ImportCommand(formats, importService));
        registry.register(new ExitCommand());

        new CommandLoop(Terminal.forStreams(in, out), new Session(), registry, wallets).run();
    }
}
