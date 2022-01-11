package com.polly.utils;

public class Location {
	private final double latitude;
	private final double longitude;
	
	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public boolean inArea(Area area) {
		return inArea(this, area);
	}
	
	public static boolean inArea(Location location, Area area) {
		double diffLat = location.getLatitude() - area.getLatitude();
		double diffLon = location.getLongitude() - area.getLongitude();
		
		double radius = area.getRadius();
		
		
		double diffSq = diffLat * diffLat + diffLon * diffLon;
		double radiusSq = radius * radius;
		
		return diffSq <= radiusSq;
	}
}
