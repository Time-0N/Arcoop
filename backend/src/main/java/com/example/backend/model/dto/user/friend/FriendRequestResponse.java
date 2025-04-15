package com.example.backend.model.dto.user.friend;

import com.example.backend.model.entity.Account;

public class FriendRequestResponse {
	public String name;

	public FriendRequestResponse(Account account) {
		name = account.getName();
	}
}

