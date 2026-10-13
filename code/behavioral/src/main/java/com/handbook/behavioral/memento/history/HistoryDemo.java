package com.handbook.behavioral.memento.history;

public class HistoryDemo {

    public static void main(String[] args) {
        BoundedHistory<String> undo = new BoundedHistory<>(3);
        String text = "";
        for (String word : new String[] {"Fresh", " mangoes", " at", " 20%", " off"}) {
            undo.push(text); // snapshot before each edit
            text += word;
        }
        System.out.println("text      : " + text);
        System.out.println("kept/drop : " + undo.size() + "/" + undo.dropped());
        while (true) {
            var previous = undo.pop();
            if (previous.isEmpty()) {
                System.out.println("no more undo (older snapshots were dropped)");
                break;
            }
            text = previous.get();
            System.out.println("undo ->   : " + text);
        }
    }
}
