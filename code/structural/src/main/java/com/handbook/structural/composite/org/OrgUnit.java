package com.handbook.structural.composite.org;

import java.util.ArrayList;
import java.util.List;

/** Component + both implementations in one place, using a sealed interface. */
public sealed interface OrgUnit permits OrgUnit.Employee, OrgUnit.Department {

    String name();

    int headcount();

    long annualCostInr();

    record Employee(String name, String title, long salaryInr) implements OrgUnit {
        public int headcount() { return 1; }
        public long annualCostInr() { return salaryInr; }
    }

    final class Department implements OrgUnit {
        private final String name;
        private final List<OrgUnit> members = new ArrayList<>();

        public Department(String name) {
            this.name = name;
        }

        public Department add(OrgUnit... units) {
            members.addAll(List.of(units));
            return this;
        }

        public String name() { return name; }

        public int headcount() {
            return members.stream().mapToInt(OrgUnit::headcount).sum();
        }

        public long annualCostInr() {
            return members.stream().mapToLong(OrgUnit::annualCostInr).sum();
        }

        public List<OrgUnit> members() {
            return List.copyOf(members);
        }
    }

    /** Pattern matching over the sealed hierarchy: an alternative to a describe() method. */
    static void print(OrgUnit unit, String indent) {
        switch (unit) { // (1)!
            case Employee e -> System.out.printf("%s%s, %s%n", indent, e.name(), e.title());
            case Department d -> {
                System.out.printf("%s[%s] headcount=%d cost=INR %.1f lakh%n",
                        indent, d.name(), d.headcount(), d.annualCostInr() / 100_000.0);
                d.members().forEach(m -> print(m, indent + "  "));
            }
        }
    }
}
