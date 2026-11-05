package com.handbook.enterprise.unitofwork.erp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Hibernate-style dirty checking: snapshot on load, compare on flush, UPDATE only what changed. */
public final class DirtyCheckingSession {

    public static final class Employee { // mutable "managed entity"
        public final String id;
        public String department;
        public long salaryPaise;

        public Employee(String id, String department, long salaryPaise) {
            this.id = id;
            this.department = department;
            this.salaryPaise = salaryPaise;
        }
    }

    private record Snapshot(String department, long salaryPaise) {
        static Snapshot of(Employee e) { return new Snapshot(e.department, e.salaryPaise); }
    }

    private final Map<String, Employee> table;
    private final Map<String, Employee> managed = new LinkedHashMap<>(); // identity map
    private final Map<String, Snapshot> snapshots = new LinkedHashMap<>();

    public DirtyCheckingSession(Map<String, Employee> table) {
        this.table = table;
    }

    public Employee find(String id) {
        return managed.computeIfAbsent(id, k -> {
            Employee row = table.get(k);
            Employee copy = new Employee(row.id, row.department, row.salaryPaise);
            snapshots.put(k, Snapshot.of(copy)); // (1)!
            return copy;
        });
    }

    /** Returns the SQL it would issue. */
    public List<String> flush() {
        List<String> sql = new ArrayList<>();
        for (Employee e : managed.values()) {
            Snapshot before = snapshots.get(e.id);
            List<String> sets = new ArrayList<>();
            if (!before.department().equals(e.department)) {
                sets.add("department='" + e.department + "'");
            }
            if (before.salaryPaise() != e.salaryPaise) {
                sets.add("salary_paise=" + e.salaryPaise);
            }
            if (!sets.isEmpty()) { // (2)!
                sql.add("UPDATE employee SET " + String.join(", ", sets) + " WHERE id='" + e.id + "'");
                table.put(e.id, new Employee(e.id, e.department, e.salaryPaise));
                snapshots.put(e.id, Snapshot.of(e));
            }
        }
        return sql;
    }
}
