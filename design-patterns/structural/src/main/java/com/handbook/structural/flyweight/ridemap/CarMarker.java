package com.handbook.structural.flyweight.ridemap;

/** Context object: small, one per car. EXTRINSIC state + a reference to the shared flyweight. */
public record CarMarker(String driverId, double lat, double lng, int heading, VehicleIcon icon) {

    public String render() {
        return driverId + ": " + icon.drawAt(lat, lng, heading);
    }
}
