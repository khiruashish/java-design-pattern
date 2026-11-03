package com.handbook.microservices.materializedview.tracking;

import com.handbook.microservices.materializedview.tracking.TrackingView.DriverAssigned;
import com.handbook.microservices.materializedview.tracking.TrackingView.EtaUpdated;
import com.handbook.microservices.materializedview.tracking.TrackingView.Shipped;

public class TrackingViewDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: One Tracking Page Built from Three Services ===");
        TrackingView view = new TrackingView();
        view.on(new DriverAssigned("AWB-77", "Ravi (KA-01-AB-1234)", 1)); // arrives before Shipped
        System.out.println("After driver event : " + view.page("AWB-77"));
        view.on(new Shipped("AWB-77", "Bengaluru", 1));
        view.on(new EtaUpdated("AWB-77", "14:40", 2));
        view.on(new EtaUpdated("AWB-77", "14:25", 1)); // older ETA delivered late
        System.out.println("After all events   : " + view.page("AWB-77"));
        System.out.println("The late, older ETA (v1) was ignored; the page answers with one key lookup, no joins across services.");
    }
}
