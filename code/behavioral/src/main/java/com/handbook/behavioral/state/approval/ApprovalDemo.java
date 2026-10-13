package com.handbook.behavioral.state.approval;

public class ApprovalDemo {

    public static void main(String[] args) {
        DocumentStatus s = DocumentStatus.DRAFT;
        s = s.submit();
        System.out.println("submitted -> " + s);
        s = s.reject();
        System.out.println("rejected  -> " + s);
        s = s.submit().approve();
        System.out.println("approved  -> " + s);
        try {
            s.approve();
        } catch (IllegalStateException e) {
            System.out.println("error     -> " + e.getMessage());
        }
        System.out.println("published -> " + s.publish());
    }
}
