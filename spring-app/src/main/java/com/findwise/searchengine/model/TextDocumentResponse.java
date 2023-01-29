package com.findwise.searchengine.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TextDocumentResponse(@JsonProperty("_id") String id,
                                   @JsonProperty("_score") Double score,
                                   @JsonProperty("_source") String source) {

    public static TextDocumentResponse.Builder of(TextDocument textDocument) {
        return new Builder(textDocument.getId(), textDocument.getSource());
    }

    public static class Builder {
        private final String id;
        private final String source;
        private Double score;

        private Builder(String id, String source) {
            this.id = id;
            this.source = source;
        }

        public Builder score(Double score) {
            this.score = score;
            return this;
        }

        public TextDocumentResponse build() {
            return new TextDocumentResponse(id, score, source);
        }
    }
}
