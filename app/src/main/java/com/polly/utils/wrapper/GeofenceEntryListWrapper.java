package com.polly.utils.wrapper;

import com.polly.geofencing.GeofenceEntry;

import java.util.List;


public class GeofenceEntryListWrapper {
	private final List<GeofenceEntry> geofenceEntries;
	
	public GeofenceEntryListWrapper(List<GeofenceEntry> geofenceEntries) {
		this.geofenceEntries = geofenceEntries;
	}
	
	public List<GeofenceEntry> getGeofenceEntries() {
		return geofenceEntries;
	}
}