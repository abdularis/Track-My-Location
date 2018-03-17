package com.github.abdularis.trackmylocation.model;

public class SharedLocation {

    private String mDevId;
    private LatLong mLocation;
    private String mName;
    private String mPhotoUrl;

    public SharedLocation() {
        mDevId = "";
        mLocation = new LatLong();
        mName = "";
        mPhotoUrl = "";
    }

    public String getDevId() {
        return mDevId;
    }

    public LatLong getLocation() {
        return mLocation;
    }

    public String getName() {
        return mName;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setDevId(String devId) {
        mDevId = devId;
    }

    public void setLocation(LatLong location) {
        mLocation = location;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public static class LatLong {
        public double latitude;
        public double longitude;

        public LatLong() {
            this(0.0, 0.0);
        }

        public LatLong(double lat, double lng) {
            latitude = lat;
            longitude = lng;
        }
    }

}
