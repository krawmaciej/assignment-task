package com.findwise.searchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TextDocument {
    @Id
    private final String id;
    private final String source;

    public TextDocument(String id, String source) {
        this.id = id;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }
}
