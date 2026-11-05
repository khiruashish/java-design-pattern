package com.handbook.structural.composite.org;

import com.handbook.structural.composite.org.OrgUnit.Department;
import com.handbook.structural.composite.org.OrgUnit.Employee;

public class OrgChartDemo {

    public static void main(String[] args) {
        System.out.println("=== HR SaaS: Org Chart Cost Roll-up ===");
        Department payments = new Department("Payments Team").add(
                new Employee("Neha", "Engineering Manager", 4_800_000),
                new Employee("Arjun", "Senior Engineer", 3_600_000),
                new Employee("Li", "Engineer", 2_400_000));
        Department search = new Department("Search Team").add(
                new Employee("Fatima", "Staff Engineer", 5_200_000),
                new Employee("Rohan", "Engineer", 2_300_000));
        Department engineering = new Department("Engineering").add(
                new Employee("Priya", "VP Engineering", 9_000_000), payments, search);

        OrgUnit.print(engineering, "");
        System.out.println("Budget check, Payments vs Engineering share: "
                + (100 * payments.annualCostInr() / engineering.annualCostInr()) + "%");
    }
}
