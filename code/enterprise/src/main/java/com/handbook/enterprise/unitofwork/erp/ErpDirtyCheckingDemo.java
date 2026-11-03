package com.handbook.enterprise.unitofwork.erp;

import com.handbook.enterprise.unitofwork.erp.DirtyCheckingSession.Employee;

import java.util.HashMap;
import java.util.Map;

public class ErpDirtyCheckingDemo {

    public static void main(String[] args) {
        System.out.println("=== ERP: Annual Appraisal with Dirty Checking ===");
        Map<String, Employee> table = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            table.put("E" + i, new Employee("E" + i, "Finance", 1_200_000_00L));
        }
        DirtyCheckingSession session = new DirtyCheckingSession(table);
        for (int i = 1; i <= 5; i++) {
            Employee e = session.find("E" + i);
            if (i == 2) {
                e.salaryPaise = 1_320_000_00L;            // 10% raise
            }
            if (i == 4) {
                e.department = "Treasury";                 // transfer
                e.salaryPaise = e.salaryPaise;             // "set" to same value: not a change
            }
        }
        System.out.println("Loaded 5 employees, flush issues:");
        session.flush().forEach(s -> System.out.println("  " + s));
        System.out.println("Second flush with no changes issues " + session.flush().size() + " statements");
    }
}
