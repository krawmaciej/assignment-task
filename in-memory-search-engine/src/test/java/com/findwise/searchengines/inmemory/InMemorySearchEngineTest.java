package com.findwise.searchengines.inmemory;

import com.findwise.IndexEntry;
import com.findwise.searchengines.inmemory.exceptions.DuplicateDocumentIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemorySearchEngineTest {

    public static Stream<Arguments> testTfIdfSortingData() {
        return Stream.of(
                Arguments.of(
                        exampleCorpus(), "brown", List.of(
                                new TfIdfIndexEntry("document1", calculateTfIdf(2/8.0, 3/2.0)),
                                new TfIdfIndexEntry("document2", calculateTfIdf(1/8.0, 3/2.0))
                        )
                ),
                Arguments.of(
                        exampleCorpus(), "fox", List.of(
                                new TfIdfIndexEntry("document3", calculateTfIdf(1/7.0, 3/2.0)),
                                new TfIdfIndexEntry("document1", calculateTfIdf(1/8.0, 3/2.0))
                        )
                ),
                Arguments.of(
                        corpusContainingNonWordCharacters(), "nisl", List.of(
                                new TfIdfIndexEntry("document3", calculateTfIdf(2/10.0, 5/2.0)),
                                new TfIdfIndexEntry("document5", calculateTfIdf(1/9.0, 5/2.0))
                        )
                ),
                Arguments.of(
                        corpusContainingNonWordCharacters(), "nunc", List.of(
                                new TfIdfIndexEntry("document5", calculateTfIdf(1/9.0, 5/4.0)),
                                new TfIdfIndexEntry("document3", calculateTfIdf(1/10.0, 5/4.0)),
                                new TfIdfIndexEntry("document2", calculateTfIdf(1/11.0, 5/4.0)),
                                new TfIdfIndexEntry("document4", calculateTfIdf(1/12.0, 5/4.0))
                        )
                )
        );
    }

    static Map<String, String> exampleCorpus() {
        return Map.of("document1", "the brown fox jumped over the brown dog",
                "document2", "the lazy brown dog sat in the corner",
                "document3", "the red fox bit the lazy dog");
    }

    static Map<String, String> corpusContainingNonWordCharacters() {
        return Map.of("document1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "document2", "Quisque consequat sed mi sit. Amet laoreet nunc-sed quam sem.",
                "document3", "Nunc vehicula nisl, non nisl ornare. Iaculis ac ac arcu",
                "document4", "Nunc tristique erat et velit porta semper maecenas lacinia, lectus vehicula quam.",
                "document5", "Duis hendrerit nunc eu scelerisque. Rutrum magna volutpat nisl!");
    }

    static double calculateTfIdf(double tf, double idfNoLog) {
        return tf * Math.log10(idfNoLog);
    }

    @ParameterizedTest
    @MethodSource("testTfIdfSortingData")
    void whenDocumentsAreIndexedAndTermIsSearched_thenResultsAreSoredByTfIdf(Map<String, String> corpus,
                                                                             String searchedTerm,
                                                                             List<IndexEntry> expectedResults) {
        // given
        InMemorySearchEngine inMemorySearchEngine = new InMemorySearchEngine();

        // when
        corpus.forEach(inMemorySearchEngine::indexDocument);
        List<IndexEntry> searchResults = inMemorySearchEngine.search(searchedTerm);

        // then
        assertEquals(expectedResults, searchResults);
    }

    @Test
    void whenNoDocumentsAreIndexedAndTermIsSearched_thenReturnNoResults() {
        // given
        InMemorySearchEngine inMemorySearchEngine = new InMemorySearchEngine();

        // when
        List<IndexEntry> searchResults = inMemorySearchEngine.search("anything");

        // then
        assertEquals(Collections.emptyList(), searchResults);
    }

    @Test
    void whenIndexedEmptyDocumentsAndTermIsSearched_thenReturnNoResults() {
        // given
        InMemorySearchEngine inMemorySearchEngine = new InMemorySearchEngine();

        // when
        inMemorySearchEngine.indexDocument("doc1", " ");
        inMemorySearchEngine.indexDocument("doc2", "");
        inMemorySearchEngine.indexDocument("doc3", "    ");
        List<IndexEntry> searchResults = inMemorySearchEngine.search("anything");

        // then
        assertEquals(Collections.emptyList(), searchResults);
    }

    @Test
    void whenIndexingDocumentsWithSameId_thenThrowException() {
        // given
        InMemorySearchEngine inMemorySearchEngine = new InMemorySearchEngine();

        // when / then
        inMemorySearchEngine.indexDocument("doc1", "a bc def");
        assertThrows(DuplicateDocumentIdException.class,
                () -> inMemorySearchEngine.indexDocument("doc1", "some other doc but same id"));
    }
}
