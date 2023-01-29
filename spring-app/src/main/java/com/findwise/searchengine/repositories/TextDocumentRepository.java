package com.findwise.searchengine.repositories;

import com.findwise.searchengine.model.TextDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TextDocumentRepository extends MongoRepository<TextDocument, String> {
}
