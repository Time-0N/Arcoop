package com.example.backend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TicTacToe {
	X(1), O(2), TIE(0);

	private Integer value;
}
