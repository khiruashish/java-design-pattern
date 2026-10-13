package com.handbook.microservices.acl.insurance;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Translates the legacy policy-admin system's XML (its own vocabulary, date format and codes)
 * into our claims service's Policy model. Only this class knows the XML exists.
 */
public final class PolicyXmlTranslator {

    public enum CoverType { COMPREHENSIVE, THIRD_PARTY }

    public record Policy(String number, String holder, CoverType cover, LocalDate validUntil, List<String> namedDrivers) {
        public boolean coversOn(LocalDate date) {
            return !date.isAfter(validUntil);
        }
    }

    private static final DateTimeFormatter LEGACY_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Policy translate(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // (1)!
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Element root = doc.getDocumentElement();
            List<String> drivers = new ArrayList<>();
            NodeList nodes = root.getElementsByTagName("DRV_NM");
            for (int i = 0; i < nodes.getLength(); i++) {
                drivers.add(nodes.item(i).getTextContent().trim());
            }
            return new Policy(
                    text(root, "POL_REF").replace("/", "-"),
                    text(root, "INS_NM"),
                    switch (text(root, "CVR_CD")) { // (2)!
                        case "01" -> CoverType.COMPREHENSIVE;
                        case "02" -> CoverType.THIRD_PARTY;
                        default -> throw new IllegalArgumentException("unknown cover code " + text(root, "CVR_CD"));
                    },
                    LocalDate.parse(text(root, "EXP_DT"), LEGACY_DATE), // (3)!
                    List.copyOf(drivers));
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot translate policy XML: " + e.getMessage(), e);
        }
    }

    private static String text(Element root, String tag) {
        return root.getElementsByTagName(tag).item(0).getTextContent().trim();
    }
}
