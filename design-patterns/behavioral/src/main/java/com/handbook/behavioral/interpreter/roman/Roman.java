package com.handbook.behavioral.interpreter.roman;

import java.util.Map;

/** Challenge solution: each symbol is a terminal expression; subtraction rule is the grammar. */
public final class Roman {

    private static final Map<Character, Integer> VALUE =
            Map.of('I', 1, 'V', 5, 'X', 10, 'L', 50, 'C', 100, 'D', 500, 'M', 1000);

    private Roman() {
    }

    public static int interpret(String numeral) {
        int total = 0;
        for (int i = 0; i < numeral.length(); i++) {
            Integer current = VALUE.get(numeral.charAt(i));
            if (current == null) {
                throw new IllegalArgumentException("Not a Roman numeral: " + numeral);
            }
            boolean subtract = i + 1 < numeral.length() && VALUE.getOrDefault(numeral.charAt(i + 1), 0) > current;
            total += subtract ? -current : current;
        }
        return total;
    }
}
