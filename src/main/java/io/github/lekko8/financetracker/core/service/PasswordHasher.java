package io.github.lekko8.financetracker.core.service;

public interface PasswordHasher {

    String hash(String password);

    boolean verify(String password, String storedHash);
}
