package com.quizmaster.backend.services;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class GameIdGenerator {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";
    public static final String alphanum = upper + lower + digits;
    public static final String easy = GameIdGenerator.digits + GameIdGenerator.upper;

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public GameIdGenerator(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public GameIdGenerator(int length, Random random) {
        this(length, random, alphanum);
    }

    public GameIdGenerator() {
        this(21);
    }

    public GameIdGenerator(int length) {
        if (length < 1) throw new IllegalArgumentException();
        this.random = new SecureRandom();
        this.symbols = GameIdGenerator.easy.toCharArray();
        this.buf = new char[length];
    }

}
