package com.findwise.searchengines.inmemory.posting;

import java.util.Comparator;

public record Posting(double tf, String id) {
    public static Comparator<Posting> comparingTf() {
        return Comparator.comparingDouble(Posting::tf);
    }
}
