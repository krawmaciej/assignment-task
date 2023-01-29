package com.findwise.searchengines.inmemory.tokenizer;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tokenizer {

    static final Pattern NON_WORD_SPLIT = Pattern.compile("\\W+");

    /**
     * Creates tokens out of provided content string.
     *
     * <p>{@link Token} contains a term and how many times it appeared in the provided content.
     * Tokens are created by splitting provided content on any non-word characters.
     *
     * @param content value to tokenize
     * @return set of tokens in no particular order
     */
    public Set<Token> tokenize(String content) {
        return NON_WORD_SPLIT.splitAsStream(content.trim())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new Token(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
}
