package com.handbook.structural.flyweight.ridemap;

/** Flyweight: heavy, immutable, shared. Holds only INTRINSIC state (what every Mini looks like). */
public final class VehicleIcon {

    private final String type;
    private final byte[] bitmap; // a 64x64 RGBA icon = 16 KB

    VehicleIcon(String type) {
        this.type = type;
        this.bitmap = new byte[64 * 64 * 4];
    }

    public String type() {
        return type;
    }

    public int sizeBytes() {
        return bitmap.length;
    }

    public String drawAt(double lat, double lng, int heading) { // (1)!
        return "%s icon at (%.4f, %.4f) facing %d deg".formatted(type, lat, lng, heading);
    }
}
