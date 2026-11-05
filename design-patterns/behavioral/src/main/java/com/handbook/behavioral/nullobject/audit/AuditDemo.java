package com.handbook.behavioral.nullobject.audit;

public class AuditDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS: Audit Log by Plan ===");
        for (String plan : new String[] {"free", "enterprise"}) {
            AuditSink sink = AuditSink.forPlan(plan);
            ProjectService service = new ProjectService(sink);
            service.createProject("neha", "Website revamp");
            service.createProject("arjun", "Q4 budget");
            service.deleteProject("arjun", "Q4 budget");
            String trail = sink instanceof AuditSink.Recording r ? r.events().toString() : "(audit log not included in plan)";
            System.out.println(plan + ": projects=" + service.projects() + ", audit=" + trail);
        }
    }
}
