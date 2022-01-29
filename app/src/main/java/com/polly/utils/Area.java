package com.polly.utils;

public class Area {
	private final double latitude;
	private final double longitude;
	private final double radius;
	
	public Area(double latitude, double longitude, double radius) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getRadius() {
		return radius;
	}
	
	public Location getCenter() {
		return new Location(latitude, longitude);
	}
}