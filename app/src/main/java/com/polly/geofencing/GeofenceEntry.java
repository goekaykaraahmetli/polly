package com.polly.geofencing;

class GeofenceEntry {
    private Area area;
    private boolean inArea;

    GeofenceEntry(Area area) {
        this.area = area;
        this.inArea = false;
    }

    void setInArea(boolean in) {
        this.inArea = in;
    }

    boolean getInArea() {
        return inArea;
    }

    Area getArea() {
        return area;
    }
}
