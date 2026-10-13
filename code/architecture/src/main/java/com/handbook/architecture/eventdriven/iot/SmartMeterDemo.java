package com.handbook.architecture.eventdriven.iot;

import com.handbook.architecture.eventdriven.iot.MeterWindowing.Reading;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public class SmartMeterDemo {

    public static void main(String[] args) {
        long t0 = Instant.parse("2026-10-14T18:00:00Z").getEpochSecond();
        var stream = new MeterWindowing(300, 6.0);

        List<Reading> readings = List.of(
                new Reading("M-17", t0 + 10, 2.1), new Reading("M-17", t0 + 130, 2.5),
                new Reading("M-42", t0 + 40, 5.8), new Reading("M-42", t0 + 200, 7.4),
                new Reading("M-17", t0 + 320, 3.0),
                new Reading("M-17", t0 + 250, 2.3),   // arrives late, but belongs to the first window
                new Reading("M-42", t0 + 410, 4.2));

        System.out.println("=== Energy: 5-minute windows over smart-meter events ===");
        readings.forEach(stream::accept);
        for (long watermark : new long[] {t0 + 300, t0 + 600}) {
            System.out.println("watermark " + time(watermark) + ":");
            stream.advanceWatermark(watermark).forEach(w -> System.out.printf("  %s window %s avg %.2f kW%s%n",
                    w.meter(), time(w.windowStart()), w.avgKw(), w.alert() ? "  ALERT: possible overload" : ""));
        }
    }

    private static String time(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond).atOffset(ZoneOffset.UTC).toLocalTime().toString();
    }
}
