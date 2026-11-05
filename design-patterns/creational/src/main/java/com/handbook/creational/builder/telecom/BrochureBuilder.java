package com.handbook.creational.builder.telecom;

/** Representation 2: marketing text for the website, from the same steps. */
public class BrochureBuilder implements PlanBuilder {

    private final StringBuilder text = new StringBuilder();

    public void name(String name) { text.append("** ").append(name.toUpperCase()).append(" **\n"); }
    public void monthlyPriceInr(int price) { text.append("  Just INR ").append(price).append("/month\n"); }
    public void data(int gbPerDay) { text.append("  ").append(gbPerDay).append(" GB high-speed data every day\n"); }
    public void unlimitedCalls() { text.append("  Unlimited calls to any network\n"); }
    public void sms(int perDay) { text.append("  ").append(perDay).append(" SMS/day\n"); }
    public void addOn(String addOn) { text.append("  + FREE ").append(addOn).append('\n'); }

    public String result() {
        return text.toString().stripTrailing();
    }
}
