package io.github.lekko8.financetracker.core.repository;

import io.github.lekko8.financetracker.core.model.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByLogin(String login);

    void save(User user);
}
