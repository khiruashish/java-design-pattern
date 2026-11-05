package com.handbook.behavioral.nullobject.audit;

/** Business code calls audit.record(...) unconditionally; the plan decides what happens. */
public class ProjectService {

    private final AuditSink audit;
    private int projects;

    public ProjectService(AuditSink audit) {
        this.audit = audit;
    }

    public void createProject(String user, String name) {
        projects++;
        audit.record(user, "created project " + name);
    }

    public void deleteProject(String user, String name) {
        projects--;
        audit.record(user, "deleted project " + name);
    }

    public int projects() {
        return projects;
    }
}
