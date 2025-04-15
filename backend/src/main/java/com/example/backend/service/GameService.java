package com.example.backend.service;


import com.example.backend.exception.InvalidGameException;
import com.example.backend.exception.InvalidParamException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.dao.Game;
import com.example.backend.model.dto.tictactoe.GameLoop;
import com.example.backend.model.dto.tictactoe.GameResponse;
import com.example.backend.model.entity.Account;
import com.example.backend.model.enums.GameStatus;
import com.example.backend.model.enums.TicTacToe;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameService {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int ROOM_ID_LENGTH = 6;
	private static final int MAX_ATTEMPTS = 10;
	private final SecureRandom random = new SecureRandom();

	private final Map<String, Game> activeGameSessions = new ConcurrentHashMap<>();

	@Autowired
	private final SimpMessagingTemplate messagingTemplate;

	public Game createGame(Account account) {
		Game game = new Game();
		game.setId(generateUniqueRoomId());
		game.setGameBoard("000000000");
		game.setPlayerOne(account);
		game.setStatus(GameStatus.NEW);
		if (Math.random() < 0.5) {
			game.setCurrentPlayerTurn(TicTacToe.X);
		} else {
			game.setCurrentPlayerTurn(TicTacToe.O);
		}

		return game;
	}

	public Game connectToGame(Account account, String id) throws InvalidParamException, InvalidGameException {
		Game game = activeGameSessions.get(id);
		if (game == null) {
			new InvalidParamException("Game with provided id doesn't exist");
		}
		if (game.getPlayerOne() == null) {
			throw new InvalidGameException("PlayerOne is not present");
		}
		if (game.getPlayerTwo() != null) {
			throw new InvalidGameException("PlayerTwo already exists");
		}
		if (game.getStatus().equals(GameStatus.IN_PROGRESS)) {
			throw new InvalidGameException("Game is already in progress");
		}
		if (game.getPlayerOne() == account) {
			throw new InvalidGameException("You can't join your own game");
		}
		game.setPlayerTwo(account);
		game.setStatus(GameStatus.IN_PROGRESS);
		messagingTemplate.convertAndSend("/topic/game/" + game.getId(), new GameResponse(game));
		return game;
	}

	public Game connectToRandomGame(Account account) {
		Game game = activeGameSessions.get(
				activeGameSessions.entrySet().stream()
						.filter(session -> session.getValue().getStatus().equals(GameStatus.IN_PROGRESS))
						.map(Map.Entry::getKey)
						.findFirst()
						.orElseThrow(() -> new NotFoundException("No active games to join found"))
		);
		game.setPlayerTwo(account);
		game.setStatus(GameStatus.IN_PROGRESS);
		messagingTemplate.convertAndSend("/topic/game/" + game.getUid(), new GameResponse(game));
		return game;
	}

	public void gameLoop(GameLoop gameLoop) throws InvalidGameException {
		Game game =activeGameSessions.get(gameLoop.getGameId());

		if (game.getCurrentPlayerTurn() != gameLoop.getType()) {
			throw new InvalidGameException("Not your turn");
		}

		if (!game.getStatus().equals(GameStatus.IN_PROGRESS)) {
			throw new InvalidGameException("Game is not in progress");
		}

		Integer[][] gameBoard = getGameBoard(gameLoop.getGameId());
		if (gameBoard[gameLoop.getCoordinateX()][gameLoop.getCoordinateY()] == 1 || gameBoard[gameLoop.getCoordinateX()][gameLoop.getCoordinateY()] == 2) {
			throw new InvalidGameException("Invalid Input");
		}
		gameBoard[gameLoop.getCoordinateX()][gameLoop.getCoordinateY()] = gameLoop.getType().getValue();

		Boolean xWinner = checkWinner(gameBoard, TicTacToe.X);
		Boolean oWinner = checkWinner(gameBoard, TicTacToe.O);

		if (xWinner) {
			game.setWinner(TicTacToe.X);
			game.getPlayerOne().incrementMedals(10);
			game.getPlayerTwo().decrementMedals(1);
			game.setStatus(GameStatus.FINISHED);
		} else if (oWinner) {
			game.setWinner(TicTacToe.O);
			game.getPlayerTwo().incrementMedals(10);
			game.getPlayerOne().decrementMedals(1);
			game.setStatus(GameStatus.FINISHED);
		} else if (checkTie(gameBoard)) {
			game.setWinner(TicTacToe.TIE);
			game.getPlayerOne().incrementMedals(5);
			game.getPlayerTwo().incrementMedals(5);
			game.setStatus(GameStatus.FINISHED);
		}

		if (game.getCurrentPlayerTurn() == TicTacToe.X) {
			game.setCurrentPlayerTurn(TicTacToe.O);
		} else {
			game.setCurrentPlayerTurn(TicTacToe.X);
		}

		game.setGameBoard(transGameBoardToString(gameBoard));

		messagingTemplate.convertAndSend("/topic/game/" + game.getUid(), new GameResponse(game));
		if (game.getStatus() == GameStatus.FINISHED) {
			activeGameSessions.remove(game.getId());
		}
	}

	private Boolean checkWinner(Integer[][] gameBoard, TicTacToe ticTacToe) {
		int[] boardArray = new int[9];
		int counterIndex = 0;
		for (int i = 0; i < gameBoard.length; i++) {
			for (int j = 0; j < gameBoard.length; j++) {
				boardArray[counterIndex] = gameBoard[i][j];
				counterIndex++;
			}
		}

		int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
		for (int i = 0; i < winCombinations.length; i++) {
			int counter = 0;
			for (int j = 0; j < winCombinations[i].length; j++) {
				if (boardArray[winCombinations[i][j]] == ticTacToe.getValue()) {
					counter++;
					if (counter == 3) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Boolean checkTie(Integer[][] gameBoard) {
		for (int i = 0; i < gameBoard.length; i++) {
			for (int j = 0; j < gameBoard.length; j++) {
				if (gameBoard[i][j] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void leaveGame(Account account) {
		Game game = findByUser(account);
		if (game.getStatus() == GameStatus.IN_PROGRESS) {
			if (game.getPlayerOne() == account) {
				game.setWinner(TicTacToe.O);
				game.getPlayerTwo().incrementMedals(10);
				game.getPlayerOne().decrementMedals(1);
				game.setStatus(GameStatus.FINISHED);
			} else {
				game.setWinner(TicTacToe.X);
				game.getPlayerOne().incrementMedals(10);
				game.getPlayerTwo().decrementMedals(1);
				game.setStatus(GameStatus.FINISHED);
			}
			messagingTemplate.convertAndSend("/topic/game/" + game.getId(), new GameResponse(game));
			activeGameSessions.remove(game.getId());
		}
		if (game.getStatus() == GameStatus.NEW) {
			game.setStatus(GameStatus.FINISHED);
			messagingTemplate.convertAndSend("/topic/game/" + game.getId(), new GameResponse(game));
			activeGameSessions.remove(game.getId());
		}
	}

	public Game findByUser(Account account) {
		return activeGameSessions.values().stream()
				.filter(session -> session.getPlayerOne().equals(account) || session.getPlayerTwo().equals(account))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No game found for this account"));
	}

	public Boolean IsUserInGame(Account account) {
		Optional<Game> game = activeGameSessions.values().stream()
				.filter(session -> session.getPlayerOne().equals(account) || session.getPlayerTwo().equals(account))
				.findFirst();

		return game.isPresent();
	}

	public Game findById(String gameId) {
		return activeGameSessions.values().stream()
				.filter(session -> session.getId().equals(gameId))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No game found with ID: " + gameId));
	}


	public Integer[][] getGameBoard(String gameId) {
		Game game = findById(gameId);
		Integer[][] gameBoard = null;
			gameBoard = new Integer[3][3];
			String gameBoardString = game.getGameBoard();
			int index = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (gameBoardString.charAt(index) == '0') {
						gameBoard[i][j] = 0;
					} else {
						char charAtIndex = gameBoardString.charAt(index);
						int intValue = Integer.parseInt(String.valueOf(charAtIndex));

						gameBoard[i][j] = intValue;
					}
					index++;
				}
			}
		return gameBoard;
	}

	public String transGameBoardToString(Integer[][] gameBoardArray) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < gameBoardArray.length; i++) {
			for (int j = 0; j < gameBoardArray[i].length; j++) {
				if (gameBoardArray[i][j] == null) {
					gameBoardArray[i][j] = 0;
				}
				builder.append(gameBoardArray[i][j]);
			}
		}
		return builder.toString();
	}

	private String generateUniqueRoomId() {
		for (int i = 0; i < MAX_ATTEMPTS; i++) {
			String candidate = generateRandomString();
			if (!activeGameSessions.containsKey(candidate)) {
				return candidate;
			}
		}
		throw new IllegalStateException("Failed to generate unique room ID after" + MAX_ATTEMPTS + " attempts");
	}

	private String generateRandomString() {
		return random.ints(ROOM_ID_LENGTH, 0, CHARACTERS.length())
				.mapToObj(CHARACTERS::charAt)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
	}
}
