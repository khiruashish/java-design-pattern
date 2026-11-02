package com.handbook.principles.oop.abstraction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** A very different implementation hidden behind the same abstraction: gzip on the way in, gunzip on the way out. */
public class CompressingStorage implements FileStorage {
    private final FileStorage delegate;

    public CompressingStorage(FileStorage delegate) { this.delegate = delegate; }

    public void put(String key, byte[] content) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gz = new GZIPOutputStream(out)) {
            gz.write(content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        delegate.put(key, out.toByteArray());
    }

    public Optional<byte[]> get(String key) {
        return delegate.get(key).map(bytes -> {
            try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                return gz.readAllBytes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public String describe() { return "gzip over " + delegate.describe(); }
}
