package com.handbook.behavioral.memento.booking;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Originator: a 4-step hotel booking wizard. Its memento is an opaque, versioned token
 * that can be stored server-side and resumed on another device.
 */
public class BookingWizard {

    private static final int FORMAT_VERSION = 2;
    private final Map<String, String> answers = new LinkedHashMap<>();
    private int step = 1;

    public void answer(String field, String value) {
        answers.put(field, value);
        step++;
    }

    public String saveDraft() { // (1)!
        StringBuilder sb = new StringBuilder("v=" + FORMAT_VERSION + ";step=" + step);
        answers.forEach((k, v) -> sb.append(';').append(k).append('=').append(v));
        return Base64.getUrlEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static BookingWizard resume(String token) {
        String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        BookingWizard w = new BookingWizard();
        for (String part : raw.split(";")) {
            String[] kv = part.split("=", 2);
            switch (kv[0]) {
                case "v" -> {
                    if (Integer.parseInt(kv[1]) != FORMAT_VERSION) { // (2)!
                        throw new IllegalArgumentException("Draft from an older app version; please start again");
                    }
                }
                case "step" -> w.step = Integer.parseInt(kv[1]);
                default -> w.answers.put(kv[0], kv[1]);
            }
        }
        return w;
    }

    @Override
    public String toString() {
        return "step " + step + "/4 " + answers;
    }
}
