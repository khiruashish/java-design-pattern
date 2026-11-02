package com.handbook.principles.other.kiss;

import java.util.List;

public class KissDemo {
    public static void main(String[] args) {
        for (int year : new int[]{1900, 2000, 2024, 2026}) {
            System.out.println(year + " leap? clever=" + KissExamples.isLeapClever(year) + " simple=" + KissExamples.isLeapSimple(year));
        }
        List<Integer> values = List.of(3, 9, 4);
        System.out.println("max: hand-rolled=" + KissExamples.maxHandRolled(values) + " library=" + KissExamples.maxSimple(values));
    }
}
