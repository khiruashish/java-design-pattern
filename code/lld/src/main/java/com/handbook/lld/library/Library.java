package com.handbook.lld.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;

/**
 * Library management LLD. Patterns: Strategy (fine policy per member type), Observer (notify the
 * next member in the reservation queue when a copy is returned).
 */
public final class Library {

    public enum MemberType { STUDENT, FACULTY }

    public record Member(String id, String name, MemberType type) {
    }

    public record Loan(String isbn, Member member, LocalDate due) {
    }

    public interface FinePolicy {
        long finePaise(Member member, long daysLate);
    }

    /** Students: ₹5/day capped at ₹200; faculty: no fines for the first 7 late days, then ₹10/day. */
    public static FinePolicy standardFines() {
        return (m, days) -> switch (m.type()) {
            case STUDENT -> Math.min(days * 500, 20_000);
            case FACULTY -> Math.max(0, days - 7) * 1_000;
        };
    }

    private final Map<String, String> titles = new HashMap<>();
    private final Map<String, Integer> available = new HashMap<>();
    private final Map<String, Queue<Member>> reservations = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();
    private final FinePolicy fines;
    private final BiConsumer<Member, String> notifier; // Observer callback (SMS/e-mail in real life)

    public Library(FinePolicy fines, BiConsumer<Member, String> notifier) {
        this.fines = fines;
        this.notifier = notifier;
    }

    public void addBook(String isbn, String title, int copies) {
        titles.put(isbn, title);
        available.merge(isbn, copies, Integer::sum);
    }

    public String borrow(Member m, String isbn, LocalDate today) {
        if (loans.stream().filter(l -> l.member().equals(m)).count() >= (m.type() == MemberType.STUDENT ? 2 : 5)) {
            return m.name() + ": loan limit reached";
        }
        Queue<Member> queue = reservations.computeIfAbsent(isbn, k -> new ArrayDeque<>());
        if (available.getOrDefault(isbn, 0) == 0) {
            if (!queue.contains(m)) {
                queue.add(m);
            }
            return m.name() + ": no copies, reservation queue position " + (List.copyOf(queue).indexOf(m) + 1);
        }
        if (!queue.isEmpty() && !queue.peek().equals(m)) {
            return m.name() + ": " + titles.get(isbn) + " is reserved for " + queue.peek().name();
        }
        queue.poll();
        available.merge(isbn, -1, Integer::sum);
        LocalDate due = today.plusDays(m.type() == MemberType.STUDENT ? 14 : 30);
        loans.add(new Loan(isbn, m, due));
        return m.name() + " borrowed " + titles.get(isbn) + ", due " + due;
    }

    public String giveBack(Member m, String isbn, LocalDate today) {
        Loan loan = loans.stream().filter(l -> l.member().equals(m) && l.isbn().equals(isbn)).findFirst().orElseThrow();
        loans.remove(loan);
        available.merge(isbn, 1, Integer::sum);
        long late = Math.max(0, ChronoUnit.DAYS.between(loan.due(), today));
        long fine = fines.finePaise(m, late);
        Queue<Member> queue = reservations.get(isbn);
        if (queue != null && !queue.isEmpty()) {
            notifier.accept(queue.peek(), titles.get(isbn) + " is ready for pickup");
        }
        return m.name() + " returned " + titles.get(isbn) + (late > 0 ? ", " + late + " days late, fine ₹" + fine / 100 : ", on time");
    }
}
