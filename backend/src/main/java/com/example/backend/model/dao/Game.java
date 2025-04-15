package com.example.backend.model.dao;

import com.example.backend.model.entity.Account;
import com.example.backend.model.enums.GameStatus;
import com.example.backend.model.enums.TicTacToe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

	private String id;
	private String uid;
	private GameStatus status;
	private String gameBoard;
	private TicTacToe winner;
	private TicTacToe currentPlayerTurn;

	private Account playerOne;

	private Account playerTwo;
}
