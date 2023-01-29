package com.findwise.searchengine.configuration;

import com.findwise.SearchEngine;
import com.findwise.searchengine.repositories.TextDocumentRepository;
import com.findwise.searchengines.inmemory.InMemorySearchEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {
    @Bean
    SearchEngine searchEngine(TextDocumentRepository repository) {
        InMemorySearchEngine inMemorySearchEngine = new InMemorySearchEngine();
        repository.findAll().forEach(doc -> inMemorySearchEngine.indexDocument(doc.getId(), doc.getSource()));
        return inMemorySearchEngine;
    }
}
