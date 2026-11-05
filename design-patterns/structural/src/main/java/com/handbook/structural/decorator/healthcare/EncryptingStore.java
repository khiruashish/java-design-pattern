package com.handbook.structural.decorator.healthcare;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Decorator: toy XOR cipher + Base64 to keep the demo readable. Use AES-GCM in real systems. */
public class EncryptingStore implements RecordStore {

    private final RecordStore inner;
    private final byte[] key;

    public EncryptingStore(RecordStore inner, String key) {
        this.inner = inner;
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void write(String id, String data) {
        inner.write(id, Base64.getEncoder().encodeToString(xor(data.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public String read(String id) {
        return new String(xor(Base64.getDecoder().decode(inner.read(id))), StandardCharsets.UTF_8);
    }

    private byte[] xor(byte[] in) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) (in[i] ^ key[i % key.length]);
        }
        return out;
    }
}
