package com.polly.utils.command.poll;

public class GetGeofencePollAreaCommand {
	private  final long id;
	
	public GetGeofencePollAreaCommand(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}