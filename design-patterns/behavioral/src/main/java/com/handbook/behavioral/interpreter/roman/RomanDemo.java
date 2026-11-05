package com.handbook.behavioral.interpreter.roman;

public class RomanDemo {

    public static void main(String[] args) {
        for (String n : new String[] {"III", "IX", "XLII", "MCMXCIV", "MMXXVI"}) {
            System.out.println(n + " = " + Roman.interpret(n));
        }
    }
}
