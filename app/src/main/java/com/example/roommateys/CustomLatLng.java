package com.example.roommateys;

public class CustomLatLng {
    private double latitude;
    private double longitude;
    public CustomLatLng() {}
    public CustomLatLng(double latitude, double longitude) {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
