package io.github.lekko8.financetracker.core.service;

import io.github.lekko8.financetracker.core.model.Wallet;
import io.github.lekko8.financetracker.core.model.WalletContent;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface WalletFormat {

    String extension();

    void write(Wallet wallet, Writer out) throws IOException;

    WalletContent read(Reader in) throws IOException;
}
