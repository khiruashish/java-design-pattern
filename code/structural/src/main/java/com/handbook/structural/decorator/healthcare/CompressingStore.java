package com.handbook.structural.decorator.healthcare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Decorator: run-length encoding, e.g. "AAAB" <-> "3A1B". Real code would use GZIP. */
public class CompressingStore implements RecordStore {

    private static final Pattern RUN = Pattern.compile("(\\d+)(\\D)");
    private final RecordStore inner;

    public CompressingStore(RecordStore inner) {
        this.inner = inner;
    }

    @Override
    public void write(String key, String data) {
        if (data.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException("demo RLE does not support digits");
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < data.length(); ) {
            int j = i;
            while (j < data.length() && data.charAt(j) == data.charAt(i)) {
                j++;
            }
            out.append(j - i).append(data.charAt(i));
            i = j;
        }
        inner.write(key, out.toString());
    }

    @Override
    public String read(String key) {
        Matcher m = RUN.matcher(inner.read(key));
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            out.append(m.group(2).repeat(Integer.parseInt(m.group(1))));
        }
        return out.toString();
    }
}
