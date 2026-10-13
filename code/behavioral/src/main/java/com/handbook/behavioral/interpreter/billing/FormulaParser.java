package com.handbook.behavioral.interpreter.billing;

/**
 * expr   := term (('+'|'-') term)*
 * term   := factor (('*'|'/') factor)*
 * factor := NUMBER | IDENT | IDENT '(' expr ',' expr ')' | '(' expr ')'
 */
public final class FormulaParser {

    private final String s;
    private int i;

    private FormulaParser(String s) {
        this.s = s.replace(" ", "");
    }

    public static Formula parse(String text) {
        FormulaParser p = new FormulaParser(text);
        Formula f = p.expr();
        if (p.i != p.s.length()) {
            throw new IllegalArgumentException("Unexpected '" + p.s.charAt(p.i) + "' at " + p.i);
        }
        return f;
    }

    private Formula expr() {
        Formula f = term();
        while (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            char op = s.charAt(i++);
            f = new Formula.BinOp(op, f, term());
        }
        return f;
    }

    private Formula term() {
        Formula f = factor();
        while (i < s.length() && (s.charAt(i) == '*' || s.charAt(i) == '/')) {
            char op = s.charAt(i++);
            f = new Formula.BinOp(op, f, factor());
        }
        return f;
    }

    private Formula factor() {
        if (s.charAt(i) == '(') {
            i++;
            Formula f = expr();
            i++; // ')'
            return f;
        }
        int start = i;
        if (Character.isDigit(s.charAt(i))) {
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                i++;
            }
            return new Formula.Num(Double.parseDouble(s.substring(start, i)));
        }
        while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) {
            i++;
        }
        String name = s.substring(start, i);
        if (i < s.length() && s.charAt(i) == '(') {
            i++;
            Formula a = expr();
            i++; // ','
            Formula b = expr();
            i++; // ')'
            return new Formula.Call(name, a, b);
        }
        return new Formula.Var(name);
    }
}
