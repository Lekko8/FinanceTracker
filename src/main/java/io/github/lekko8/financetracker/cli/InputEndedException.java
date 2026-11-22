package io.github.lekko8.financetracker.cli;

public class InputEndedException extends RuntimeException {

    public InputEndedException() {
        super("Ввод завершён.");
    }
}
