package com.handbook.structural.adapter.logistics;

/** Adaptee 1: an international courier that returns an XML-ish string. */
public class GlobalExpressClient {

    public String getShipmentXml(String awb) {
        return "<shipment awb='" + awb + "'><event code='OFD' city='Pune'/></shipment>";
    }
}
