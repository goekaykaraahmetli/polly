package com.polly.utils.wrapper;

import java.util.Collections;
import java.util.List;

import com.polly.utils.poll.BasicPollInformation;
import com.polly.utils.usergroup.UsergroupDescription;

public class UsergroupWrapper {
	private final long id;
	private final String name;
	
	private final List<UserWrapper> members;
	private final List<UserWrapper> admins;
	
	private final UsergroupDescription description;
	
	private final List<BasicPollInformation> polls;
	
	public UsergroupWrapper(long id, String name, List<UserWrapper> members, List<UserWrapper> admins, UsergroupDescription description, List<BasicPollInformation> polls) {
		this.id = id;
		this.name = name;
		this.members = Collections.unmodifiableList(members);
		this.admins = Collections.unmodifiableList(admins);
		this.description = description;
		this.polls = Collections.unmodifiableList(polls);
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<UserWrapper> getMembers() {
		return members;
	}

	public List<UserWrapper> getAdmins() {
		return admins;
	}

	public UsergroupDescription getDescription() {
		return description;
	}

	public List<BasicPollInformation> getPolls() {
		return polls;
	}
}