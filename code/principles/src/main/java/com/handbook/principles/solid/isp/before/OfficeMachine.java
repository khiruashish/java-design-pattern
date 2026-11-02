package com.handbook.principles.solid.isp.before;

/** VIOLATES ISP: a "fat" interface. Every implementer must provide every capability. */
public interface OfficeMachine {
    String print(String document);

    String scan(String document);

    String fax(String document, String number);
}
