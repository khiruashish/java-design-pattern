package com.handbook.behavioral.iterator.statement;

import com.handbook.behavioral.iterator.statement.StatementApi.Page;
import com.handbook.behavioral.iterator.statement.StatementApi.Txn;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Iterable view over ALL transactions; pages are fetched lazily as the caller iterates. */
public class Transactions implements Iterable<Txn> {

    private final StatementApi api;
    private final String account;

    public Transactions(StatementApi api, String account) {
        this.api = api;
        this.account = account;
    }

    @Override
    public Iterator<Txn> iterator() {
        return new Iterator<>() { // (1)!
            private Iterator<Txn> current = Collections.emptyIterator();
            private String cursor;
            private boolean lastPageLoaded;

            @Override
            public boolean hasNext() {
                while (!current.hasNext() && !lastPageLoaded) { // (2)!
                    Page page = api.fetch(account, cursor);
                    current = page.items().iterator();
                    cursor = page.nextCursor();
                    lastPageLoaded = cursor == null;
                }
                return current.hasNext();
            }

            @Override
            public Txn next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current.next();
            }
        };
    }

    public Stream<Txn> stream() {
        return StreamSupport.stream(spliterator(), false); // (3)!
    }
}
