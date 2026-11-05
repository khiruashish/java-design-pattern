package com.handbook.behavioral.interpreter.billing;

import java.util.Map;

/** Arithmetic expression AST: numbers, variables, + - * /, and max/min functions. */
public sealed interface Formula {

    double eval(Map<String, Double> vars);

    record Num(double value) implements Formula {
        public double eval(Map<String, Double> v) { return value; }
    }

    record Var(String name) implements Formula {
        public double eval(Map<String, Double> v) {
            Double value = v.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Unknown variable: " + name);
            }
            return value;
        }
    }

    record BinOp(char op, Formula left, Formula right) implements Formula {
        public double eval(Map<String, Double> v) {
            double a = left.eval(v), b = right.eval(v);
            return switch (op) {
                case '+' -> a + b;
                case '-' -> a - b;
                case '*' -> a * b;
                case '/' -> a / b;
                default -> throw new IllegalStateException();
            };
        }
    }

    record Call(String fn, Formula a, Formula b) implements Formula {
        public double eval(Map<String, Double> v) {
            return switch (fn) {
                case "max" -> Math.max(a.eval(v), b.eval(v));
                case "min" -> Math.min(a.eval(v), b.eval(v));
                default -> throw new IllegalArgumentException("Unknown function: " + fn);
            };
        }
    }
}
