package com.polly.utils.command;

import com.polly.utils.Location;

public class GetGeofencesCommand {
	private final Location location;
	
	public GetGeofencesCommand(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
}