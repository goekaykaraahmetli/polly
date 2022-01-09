package com.polly.utils.wrapper;

import java.util.Collections;
import java.util.List;

public class UsergroupListWrapper {
	private final List<UsergroupWrapper> usergroupList;
	
	public UsergroupListWrapper(List<UsergroupWrapper> usergroupList) {
		this.usergroupList = Collections.unmodifiableList(usergroupList);
	}

	public List<UsergroupWrapper> getUsergroupList() {
		return usergroupList;
	}
}