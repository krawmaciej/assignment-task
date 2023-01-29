package com.findwise.searchengine.services;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import com.findwise.searchengine.model.SearchResponse;
import com.findwise.searchengine.model.TextDocument;
import com.findwise.searchengine.model.TextDocumentResponse;
import com.findwise.searchengine.repositories.TextDocumentRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TextDocumentService {

    private final TextDocumentRepository textDocumentRepository;
    private final SearchEngine searchEngine;

    public TextDocumentService(TextDocumentRepository textDocumentRepository, SearchEngine searchEngine) {
        this.textDocumentRepository = textDocumentRepository;
        this.searchEngine = searchEngine;
    }

    public TextDocumentResponse insertTextDocument(String text) {
        return insertTextDocument(null, text);
    }

    /**
     * Persists and indexes the text document. Will not accept the same id more than once.
     *
     * <p>If id is null then it will be automatically generated and returned from this method.
     *
     * @param id id of the document to be persisted and indexed
     * @param text text of the document to be persisted and indexed
     * @return {@link TextDocumentResponse} containing id and text source of persisted text document
     * @throws ResponseStatusException when id was already persisted
     */
    public TextDocumentResponse insertTextDocument(String id, String text) {
        try {
            TextDocument entity = textDocumentRepository.insert(new TextDocument(id, text));
            searchEngine.indexDocument(entity.getId(), entity.getSource());
            return TextDocumentResponse.of(entity).build();
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Text document with id " + id + " already exists.", ex);
        }
    }

    /**
     * Retrieves persisted text document by its id.
     * Will throw an exception if document with requested id was not found.
     *
     * @param id id of the document to be retrieved
     * @return {@link TextDocumentResponse} containing id and text source of persisted text document
     * @throws ResponseStatusException if requested document was not found
     */
    public TextDocumentResponse getTextDocument(String id) {
        var entity = textDocumentRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "TextDocument with id " + id + " not found."));
        return TextDocumentResponse.of(entity).build();
    }

    /**
     * Searches for the documents that contain requested term.
     *
     * <p>Retrieved documents will be sorted by their TfIdf values,
     * if no documents contain the term then will return an empty array.
     *
     * @param term term to search for
     * @return list of documents containing the term, sorted by TfIdf
     */
    public SearchResponse<TextDocumentResponse> findTextDocuments(String term) {
        List<IndexEntry> entries = searchEngine.search(term);
        List<TextDocument> allById = textDocumentRepository.findAllById(
                entries.stream().map(IndexEntry::getId).toList()
        );
        Map<String, TextDocument> idToDocumentEntity = allById.stream()
                .collect(Collectors.toMap(TextDocument::getId, id -> id));
        var hits = entries.stream()
                .map(entry ->
                        TextDocumentResponse.of(idToDocumentEntity.get(entry.getId()))
                                .score(entry.getScore())
                                .build())
                .toList();
        return new SearchResponse<>(hits.size(), hits);
    }
}
