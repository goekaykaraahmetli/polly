package com.polly.geofencing;

import com.polly.utils.Area;

public class GeofenceEntry {
    private Area area;
    private boolean inArea;
    private final long id;

    public GeofenceEntry(Area area, long id) {
        this.area = area;
        this.inArea = false;
        this.id = id;
    }

    void setInArea(boolean in) {
        this.inArea = in;
    }

    public boolean getInArea() {
        return inArea;
    }

    public Area getArea() {
        return area;
    }

    public long getId() {
        return id;
    }
}