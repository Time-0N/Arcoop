package com.example.backend.model.dto.user;

import com.example.backend.model.entity.Account;

import java.math.BigDecimal;

public class UserResponse {
	public String name;
	public BigDecimal medals;
	public String equippedColor;

	public UserResponse(Account account) {
		name = account.getName();
		medals = account.getMedals();
		equippedColor = account.getEquippedColor();
	}
}