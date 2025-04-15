package com.example.backend.model.dto.tictactoe;

import com.example.backend.model.dao.Game;
import com.example.backend.model.enums.GameStatus;
import com.example.backend.model.enums.TicTacToe;

public class GameResponse {
	public String id;
	public GameStatus status;
	public String gameBoard;
	public TicTacToe winner;
	public TicTacToe currentPlayerTurn;
	public String playerX;
	public String playerXColor;
	public String playerO;
	public String playerOColor;

	public GameResponse (Game game) {
		id = game.getUid();
		status = game.getStatus();
		gameBoard = game.getGameBoard();
		winner = game.getWinner();
		currentPlayerTurn = game.getCurrentPlayerTurn();
		if (game.getPlayerOne() != null) {
			playerX = game.getPlayerOne().getName();
			playerXColor = game.getPlayerOne().getEquippedColor();
		}
		if (game.getPlayerTwo() != null) {
			playerO = game.getPlayerTwo().getName();
			playerOColor = game.getPlayerTwo().getEquippedColor();
		}
	}
}
