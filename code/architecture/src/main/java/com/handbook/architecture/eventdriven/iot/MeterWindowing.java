package com.handbook.architecture.eventdriven.iot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stream processing on smart-meter readings: group events into 5-minute tumbling windows per meter
 * (by EVENT time, not arrival time), emit the average when a window closes, and raise an alert
 * when the average crosses a threshold.
 */
public final class MeterWindowing {

    public record Reading(String meter, long epochSecond, double kw) {
    }

    public record WindowResult(String meter, long windowStart, double avgKw, boolean alert) {
    }

    private final long windowSeconds;
    private final double alertKw;
    private final Map<String, TreeMap<Long, List<Double>>> open = new TreeMap<>();

    public MeterWindowing(long windowSeconds, double alertKw) {
        this.windowSeconds = windowSeconds;
        this.alertKw = alertKw;
    }

    public void accept(Reading r) {
        long windowStart = r.epochSecond() - r.epochSecond() % windowSeconds; // (1)!
        open.computeIfAbsent(r.meter(), m -> new TreeMap<>())
                .computeIfAbsent(windowStart, w -> new ArrayList<>()).add(r.kw());
    }

    /** Close every window that ended before the watermark (how far event time has progressed). */
    public List<WindowResult> advanceWatermark(long watermark) { // (2)!
        List<WindowResult> closed = new ArrayList<>();
        open.forEach((meter, windows) -> {
            while (!windows.isEmpty() && windows.firstKey() + windowSeconds <= watermark) {
                var w = windows.pollFirstEntry();
                double avg = w.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                closed.add(new WindowResult(meter, w.getKey(), Math.round(avg * 100) / 100.0, avg > alertKw));
            }
        });
        return closed;
    }
}
