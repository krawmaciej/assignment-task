package com.findwise.searchengines.inmemory.exceptions;

public class DuplicateDocumentIdException extends RuntimeException {
    public DuplicateDocumentIdException(String message) {
        super(message);
    }
}
