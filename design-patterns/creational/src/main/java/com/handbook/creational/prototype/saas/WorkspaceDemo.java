package com.handbook.creational.prototype.saas;

public class WorkspaceDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS: New Team Workspaces from a Template ===");
        Board template = new Board("Template: Product Launch")
                .column("To Do", new Task("Write PRD", "PM"), new Task("Threat model", "Security"))
                .column("Doing")
                .column("Done");

        Board payments = template.copyAs("Payments Team Launch")
                .addTask("To Do", new Task("PCI checklist", "Compliance"));
        Board search = template.copyAs("Search Team Launch");

        System.out.println(payments);
        System.out.println(search);
        System.out.println("Template still has " + template.taskCount() + " tasks");

        System.out.println("--- with a shallow copy (the bug) ---");
        Board buggy = template.shallowCopyAs("Buggy Team Launch")
                .addTask("To Do", new Task("Team-only task", "Dev"));
        System.out.println("Template now has " + template.taskCount() + " tasks (leaked from " + buggy.taskCount() + "-task copy)");
    }
}
