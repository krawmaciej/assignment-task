package com.findwise.searchengines.inmemory;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import com.findwise.searchengines.inmemory.exceptions.DuplicateDocumentIdException;
import com.findwise.searchengines.inmemory.posting.Posting;
import com.findwise.searchengines.inmemory.posting.PostingList;
import com.findwise.searchengines.inmemory.tokenizer.Token;
import com.findwise.searchengines.inmemory.tokenizer.Tokenizer;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in memory implementation of {@link SearchEngine}.
 *
 * <p>This search engine does not support updating the index and will throw
 * a {@link DuplicateDocumentIdException} when document with the same id was already indexed.
 *
 * <p>This search engine will also not search through documents that consist only of non-word tokens.
 */
public class InMemorySearchEngine implements SearchEngine {

    private final Tokenizer tokenizer = new Tokenizer();
    private final ConcurrentHashMap.KeySetView<String, Boolean> indexedDocuments = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, PostingList> invertedIndex = new ConcurrentHashMap<>();

    @Override
    public void indexDocument(String id, String content) {
        if (!indexedDocuments.add(id)) {
            throw new DuplicateDocumentIdException("Document with id: " + id + " is already indexed.");
        }
        Set<Token> tokens = tokenizer.tokenize(content);

        if (tokens.isEmpty()) {
            return; // don't index document without meaningful tokens to avoid division by 0
        }

        long totalCount = tokens.stream().mapToLong(Token::count).sum();
        for (Token token : tokens) {
            PostingList postingList = invertedIndex.computeIfAbsent(token.term(), key -> new PostingList());
            double tf = calculateTf(totalCount, token);
            postingList.add(new Posting(tf, id));
        }
    }

    @Override
    public List<IndexEntry> search(String term) {
        PostingList postingList = invertedIndex.get(term);
        if (postingList != null) {
            double idf = calculateIdf(postingList);
            return postingList.stream()
                    .map(tfDoc -> new TfIdfIndexEntry(tfDoc.id(), calculateTfIdf(tfDoc.tf(), idf)))
                    .map(IndexEntry.class::cast)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    private static double calculateTf(long totalCount, Token token) {
        return token.count() / (double) totalCount;
    }

    private double calculateIdf(PostingList postingList) {
        return Math.log10(indexedDocuments.size() / (double) postingList.size());
    }

    private double calculateTfIdf(double tf, double idf) {
        return tf * idf;
    }
}
