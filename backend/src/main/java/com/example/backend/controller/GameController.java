package com.example.backend.controller;


import com.example.backend.exception.InvalidGameException;
import com.example.backend.exception.InvalidParamException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.dao.Game;
import com.example.backend.model.dto.tictactoe.GameLoop;
import com.example.backend.model.dto.tictactoe.GameResponse;
import com.example.backend.model.dto.tictactoe.Move;
import com.example.backend.model.entity.Account;
import com.example.backend.model.enums.TicTacToe;
import com.example.backend.service.AccountService;
import com.example.backend.service.GameService;
import com.example.backend.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/games")
public class GameController {

	private final GameService gameService;
	private final JwtService jwtService;
	private final AccountService accountService;

	@PostMapping("/create-game")
	public ResponseEntity<GameResponse> start(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		Game game = gameService.createGame(account);
		return ResponseEntity.ok(new GameResponse(game));
	}

	@PostMapping("/connect/{uid}")
	public ResponseEntity<GameResponse> connect(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String uid) throws InvalidParamException, InvalidGameException {
		Account account = getAccountFromToken(authorizationHeader);
		Game game = gameService.connectToGame(account, uid);
		return ResponseEntity.ok(new GameResponse(game));
	}

	@PostMapping("/connect/random")
	public ResponseEntity<GameResponse> connectRandom(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		Game game = gameService.connectToRandomGame(account);
		return ResponseEntity.ok(new GameResponse(game));
	}

	@PostMapping("/gameLoop")
	public void gameLoop(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Move move) throws InvalidGameException {
		Account account = getAccountFromToken(authorizationHeader);
		Game game = gameService.findByUser(account);

		if (game.getPlayerOne() == account) {
			gameService.gameLoop(new GameLoop(TicTacToe.X, move.getCoordinateX(), move.getCoordinateY(), game.getUid()));
		} else if (game.getPlayerTwo() == account) {
			gameService.gameLoop(new GameLoop(TicTacToe.O, move.getCoordinateX(), move.getCoordinateY(), game.getUid()));
		}
	}

	@GetMapping("/game-reconnect")
	public ResponseEntity<GameResponse> gameReconnect(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		Game game = gameService.findByUser(account);

		return ResponseEntity.ok(new GameResponse(game));
	}

	@GetMapping("/game/check-for-game")
	public ResponseEntity<Boolean> checkForGame(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		return ResponseEntity.ok(gameService.IsUserInGame(account));
	}

	@PostMapping("/game-surrender")
	public void surrender(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		gameService.leaveGame(account);
	}

	private Account getAccountFromToken(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String username = jwtService.extractUsername(token);
		return accountService.findByUsername(username).orElseThrow(() -> new NotFoundException("Account doesn't exist"));
	}
}

