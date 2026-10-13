package com.handbook.behavioral.interpreter.promo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recursive-descent parser. Grammar:
 * <pre>
 *   expr    := term ( OR term )*
 *   term    := factor ( AND factor )*
 *   factor  := NOT factor | '(' expr ')' | compare
 *   compare := IDENT OP ( NUMBER | 'string' )
 * </pre>
 */
public final class RuleParser {

    private static final Pattern TOKEN = Pattern.compile(
            "\\s*(?:(\\d+(?:\\.\\d+)?)|'([^']*)'|(>=|<=|==|!=|>|<|\\(|\\))|([A-Za-z_][A-Za-z_0-9]*))");

    private final List<String> tokens = new ArrayList<>();
    private int pos;

    private RuleParser(String text) {
        Matcher m = TOKEN.matcher(text);
        int end = 0;
        while (m.lookingAt()) {
            if (m.group(2) != null) {
                tokens.add("'" + m.group(2)); // mark strings with a leading quote
            } else {
                tokens.add(m.group().strip());
            }
            end = m.end();
            m.region(end, text.length());
        }
        if (!text.substring(end).isBlank()) {
            throw new IllegalArgumentException("Unexpected input at: " + text.substring(end));
        }
    }

    public static Rule parse(String text) {
        RuleParser p = new RuleParser(text);
        Rule rule = p.expr();
        if (p.pos != p.tokens.size()) {
            throw new IllegalArgumentException("Unexpected token: " + p.tokens.get(p.pos));
        }
        return rule;
    }

    private Rule expr() {
        Rule left = term();
        while (accept("OR")) {
            left = new Rule.Or(left, term());
        }
        return left;
    }

    private Rule term() {
        Rule left = factor();
        while (accept("AND")) {
            left = new Rule.And(left, factor());
        }
        return left;
    }

    private Rule factor() {
        if (accept("NOT")) {
            return new Rule.Not(factor());
        }
        if (accept("(")) {
            Rule inner = expr();
            expect(")");
            return inner;
        }
        String field = next();
        String op = next();
        String raw = next();
        Object value = raw.startsWith("'") ? raw.substring(1) : Double.valueOf(raw);
        return new Rule.Compare(field, op, value);
    }

    private boolean accept(String token) {
        if (pos < tokens.size() && tokens.get(pos).equalsIgnoreCase(token)) {
            pos++;
            return true;
        }
        return false;
    }

    private void expect(String token) {
        if (!accept(token)) {
            throw new IllegalArgumentException("Expected " + token);
        }
    }

    private String next() {
        if (pos >= tokens.size()) {
            throw new IllegalArgumentException("Unexpected end of rule");
        }
        return tokens.get(pos++);
    }
}
