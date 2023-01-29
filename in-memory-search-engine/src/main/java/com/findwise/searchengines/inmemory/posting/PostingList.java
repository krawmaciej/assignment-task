package com.findwise.searchengines.inmemory.posting;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

public class PostingList {

    private final ConcurrentSkipListSet<Posting> values =
            new ConcurrentSkipListSet<>(Posting.comparingTf().reversed());

    public void add(Posting posting) {
        values.add(posting);
    }

    public int size() {
        return values.size();
    }

    public Stream<Posting> stream() {
        return values.stream();
    }
}
