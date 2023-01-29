package com.findwise.searchengines.inmemory.tokenizer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenizerTest {

    public static Stream<Arguments> nonWordContents() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("  "),
                Arguments.of(","),
                Arguments.of(" ,. ")
        );
    }

    @ParameterizedTest
    @MethodSource("nonWordContents")
    void whenNoMeaningfulTokensExist_thenShouldReturnEmptySet(String content) {
        // given
        Tokenizer tokenizer = new Tokenizer();

        // when
        Set<Token> result = tokenizer.tokenize(content);

        // then
        assertTrue(result.isEmpty());
    }
}
