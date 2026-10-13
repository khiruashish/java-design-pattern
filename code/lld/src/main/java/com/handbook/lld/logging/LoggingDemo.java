package com.handbook.lld.logging;

import com.handbook.lld.logging.MiniLogger.Appender;
import com.handbook.lld.logging.MiniLogger.Level;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class LoggingDemo {

    public static void main(String[] args) {
        Clock clock = Clock.fixed(Instant.parse("2026-10-14T10:15:30Z"), ZoneOffset.UTC);
        List<String> alertsSent = new ArrayList<>();

        Appender console = new Appender("console", Level.DEBUG,
                r -> Instant.ofEpochMilli(r.epochMillis()).toString().substring(11, 19) + " " + r.level() + " " + r.logger() + " - " + r.message(),
                System.out::println);
        Appender json = new Appender("file", Level.INFO,
                r -> "{\"level\":\"" + r.level() + "\",\"logger\":\"" + r.logger() + "\",\"msg\":\"" + r.message() + "\"}",
                System.out::println);
        Appender pager = new Appender("pager", Level.ERROR, r -> r.logger() + ": " + r.message(), alertsSent::add);
        console.then(json).then(pager);

        var logger = new MiniLogger(clock, console, Level.INFO);
        logger.setLevel("com.shop.payment", Level.DEBUG);

        System.out.println("=== LLD: Logging framework (chain of appenders, logger hierarchy) ===");
        logger.log("com.shop.cart", Level.DEBUG, "cart recalculated");      // below INFO for com.shop: dropped
        logger.log("com.shop.cart", Level.INFO, "item added MANGO-1KG");
        logger.log("com.shop.payment.upi", Level.DEBUG, "UPI intent created"); // inherits DEBUG from com.shop.payment
        logger.log("com.shop.payment", Level.ERROR, "PayFast timeout for ORD-9");
        System.out.println("pages sent to on-call: " + alertsSent);
    }
}
