package com.pojul.fastIM.entity;

public class LatLonRange {

    private double minLat;
    private double minLon;
    private double maxLat;
    private double maxLon;

    private double rawLat;
    private double rawLon;

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }

    public double getRawLat() {
        return rawLat;
    }

    public void setRawLat(double rawLat) {
        this.rawLat = rawLat;
    }

    public double getRawLon() {
        return rawLon;
    }

    public void setRawLon(double rawLon) {
        this.rawLon = rawLon;
    }
}
