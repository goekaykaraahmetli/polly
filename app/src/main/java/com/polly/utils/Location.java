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
		return getDistance(location, area.getCenter()) < area.getRadius();
	}
	
	public double getDistanceToArea(Area area) {
		return getDistanceToArea(this, area);
	}
	
	public static double getDistanceToArea(Location location, Area area) {
		return getDistance(location, area.getCenter()) - area.getRadius();
	}
	
	public double getDistance(Location otherLocation) {
		return getDistance(this, otherLocation);
	}
	
	public static double getDistance(Location location1, Location location2) {
		final double lat1 = location1.getLatitude();
		final double lat2 = location2.getLatitude();
		
		final double lon1 = location1.getLongitude();
		final double lon2 = location2.getLongitude();
		
		final double phi1 = lat1 * Math.PI/180.0;
		final double phi2 = lat2 * Math.PI/180.0;
		
		final double deltaPhi = (lat2-lat1) * Math.PI/180.0;
		final double deltaLambda = (lon2 - lon1) * Math.PI/180.0;
		
		final double R = 6371000.0;
		
		// Haversine formula: also look at "https://www.movable-type.co.uk/scripts/latlong.html"
		final double a = Math.sin(deltaPhi/2.0) * Math.sin(deltaPhi/2.0) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda/2.0) * Math.sin(deltaLambda/2.0);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		final double d = R * c;
		
		return d;
	}
}
