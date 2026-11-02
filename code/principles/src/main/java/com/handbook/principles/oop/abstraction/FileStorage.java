package com.handbook.principles.oop.abstraction;

import java.util.Optional;

/** Abstraction: callers see WHAT storage does, not HOW (disk, memory, S3...). */
public interface FileStorage {
    void put(String key, byte[] content);

    Optional<byte[]> get(String key);

    String describe();
}
