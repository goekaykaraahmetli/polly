package com.polly.utils.wrapper;

import java.util.Collections;
import java.util.List;

public class UserListWrapper {
	private final List<UserWrapper> userList;
	
	public UserListWrapper(List<UserWrapper> userList) {
		this.userList = Collections.unmodifiableList(userList);
	}

	public List<UserWrapper> getUserList() {
		return userList;
	}
}