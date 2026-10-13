package com.handbook.microservices.acl.insurance;

import java.time.LocalDate;

public class PolicyAclDemo {

    public static void main(String[] args) {
        String legacyXml = """
                <PLCY_RSP>
                  <POL_REF>MTR/2025/004417</POL_REF>
                  <INS_NM>Ravi Kumar</INS_NM>
                  <CVR_CD>01</CVR_CD>
                  <EXP_DT>20261231</EXP_DT>
                  <DRVRS><DRV_NM>Ravi Kumar</DRV_NM><DRV_NM>Anita Kumar</DRV_NM></DRVRS>
                </PLCY_RSP>
                """;
        var translator = new PolicyXmlTranslator();

        System.out.println("=== Insurance: claims service reads policies from a legacy XML system ===");
        var policy = translator.translate(legacyXml);
        System.out.println("Policy: " + policy);
        System.out.println("Accident on 2026-11-02 covered? " + policy.coversOn(LocalDate.of(2026, 11, 2)));
        System.out.println("Accident on 2027-01-05 covered? " + policy.coversOn(LocalDate.of(2027, 1, 5)));
        try {
            translator.translate(legacyXml.replace("<CVR_CD>01", "<CVR_CD>07"));
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown code rejected at the boundary: " + e.getMessage());
        }
    }
}
