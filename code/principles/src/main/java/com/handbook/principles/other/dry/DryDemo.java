package com.handbook.principles.other.dry;

public class DryDemo {
    public static void main(String[] args) {
        SignupBefore before = new SignupBefore();
        String bad = "@nobody";
        System.out.println("before: register(" + bad + ")=" + before.canRegister(bad) + ", invite(" + bad + ")=" + before.canInvite(bad)
                + "  <-- two copies, two answers");
        System.out.println("after : isValid(" + bad + ")=" + EmailAddress.isValid(bad));
        EmailAddress ok = new EmailAddress("  Ana@Example.COM ");
        System.out.println("after : normalised=" + ok.value() + ", domain=" + ok.domain());
    }
}
