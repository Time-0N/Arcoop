package com.example.backend.model.dto.tictactoe;

import com.example.backend.model.enums.TicTacToe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameLoop {

	private TicTacToe type;
	private Integer coordinateX;
	private Integer coordinateY;
	private String gameId;
}
