package com.handbook.behavioral.interpreter.hours;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Interprets expressions like "MON-FRI 09:00-18:00, SAT 10:00-14:00".
 * Grammar: schedule := rule (',' rule)* ; rule := days ' ' time '-' time ; days := DAY | DAY '-' DAY
 */
public final class OpeningHours {

    private record Window(Set<DayOfWeek> days, LocalTime from, LocalTime to) { // terminal-level expression
        boolean contains(DayOfWeek day, LocalTime t) {
            return days.contains(day) && !t.isBefore(from) && t.isBefore(to);
        }
    }

    private final List<Window> windows = new ArrayList<>(); // non-terminal: OR of windows

    private OpeningHours() {
    }

    public static OpeningHours parse(String text) {
        OpeningHours h = new OpeningHours();
        for (String rule : text.split(",")) {
            String[] parts = rule.strip().split(" ");
            String[] times = parts[1].split("-");
            h.windows.add(new Window(days(parts[0]), LocalTime.parse(times[0]), LocalTime.parse(times[1])));
        }
        return h;
    }

    private static Set<DayOfWeek> days(String spec) {
        String[] range = spec.split("-");
        DayOfWeek from = day(range[0]);
        DayOfWeek to = range.length == 2 ? day(range[1]) : from;
        Set<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek d = from; ; d = d.plus(1)) {
            result.add(d);
            if (d == to) {
                return result;
            }
        }
    }

    private static DayOfWeek day(String abbrev) {
        for (DayOfWeek d : DayOfWeek.values()) {
            if (d.name().startsWith(abbrev)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown day: " + abbrev);
    }

    public boolean isOpen(DayOfWeek day, LocalTime time) {
        return windows.stream().anyMatch(w -> w.contains(day, time));
    }
}
