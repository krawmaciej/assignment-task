package com.findwise.searchengine.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import com.findwise.searchengine.model.SearchResponse;
import com.findwise.searchengine.model.TextDocumentResponse;
import com.findwise.searchengine.services.TextDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text")
public class TextController {

    private final TextDocumentService textDocumentService;

    public TextController(TextDocumentService textDocumentService) {
        this.textDocumentService = textDocumentService;
    }

    @PostMapping("/_doc")
    @ResponseStatus(HttpStatus.CREATED)
    public TextDocumentResponse createWithoutId(@RequestBody TextNode text) {
        return textDocumentService.insertTextDocument(text.textValue());
    }

    @RequestMapping(value = "/_create/{id}", method = { RequestMethod.PUT, RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public TextDocumentResponse createWithId(@PathVariable("id") String id, @RequestBody TextNode text) {
        return textDocumentService.insertTextDocument(id, text.textValue());
    }

    @GetMapping("/_doc/{id}")
    public TextDocumentResponse retrieveTextDocument(@PathVariable("id") String id) {
        return textDocumentService.getTextDocument(id);
    }

    @GetMapping("/_search")
    public SearchResponse<TextDocumentResponse> findTextDocuments(@RequestParam("q") String term) {
        return textDocumentService.findTextDocuments(term);
    }
}
