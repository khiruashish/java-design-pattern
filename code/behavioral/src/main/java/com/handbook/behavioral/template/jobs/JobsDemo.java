package com.handbook.behavioral.template.jobs;

public class JobsDemo {

    public static void main(String[] args) {
        new ScheduledJob() {
            protected String name() { return "invoice-generation"; }
            protected void doWork() { System.out.println("  generated 1,240 invoices"); }
        }.execute();

        new ScheduledJob() {
            protected String name() { return "cache-warmup"; }
            protected boolean shouldRun() { return false; } // disabled during the sale freeze
            protected void doWork() { }
        }.execute();

        new ScheduledJob() {
            protected String name() { return "gst-report"; }
            protected void doWork() { throw new IllegalStateException("tax service unavailable"); }
        }.execute();
        System.out.println("gst-report lock released? " + !ScheduledJob.isLocked("gst-report"));
    }
}
