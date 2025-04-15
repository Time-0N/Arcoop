import './TicTacToe.css';
import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";

type Move = {
    coordinateX: number
    coordinateY: number
}

type Game = {
    id: string | null
    status: "NEW" | "IN_PROGRESS" | "FINISHED"
    gameBoard: string
    winner: "X" | "O" | "TIE" | null
    currentPlayerTurn: "X" | "O" | undefined
    playerX: string
    playerXColor: string
    playerO: string
    playerOColor: string
}

const TicTacToe = () => {
    const [gameId, setGameId] = useState<string>();
    const [gameInProgress, setGameInProgress] = useState<boolean>(false);
    const [game, setGame] = useState<Game>({gameBoard: "000000000", id: null, status: "NEW", winner: null , currentPlayerTurn: undefined, playerX: "Searching...", playerXColor: "#FFFFFF", playerO: "Searching...", playerOColor: "#FFFFFF"});
    const [client] = useState(new Client({
        brokerURL: "wss://arcoop.onrender.com/ws",
        debug: function (str: string) {
            console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    }));

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch("/api/games/game/check-for-game", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + localStorage.getItem("jwt"),
                },
            });

            const data = await response.json();
            console.log(data);
            setGameInProgress(data);
        };

        fetchData();
    }, []);

    useEffect(() => {
        client.activate();
        client.onConnect = () => {
            console.log("Connected to WebSocket Server");
        };
        client.onDisconnect = () => {
            console.log("Disconnected from WebSocket Server");
        };
        return () => {
            client.deactivate();
        };
    }, [client]);

    if (game.id) {
        console.log(game.id)
        client.subscribe("/topic/game/" + game.id, (message) => {
            const newGame: Game = JSON.parse(message.body);
            setGame(newGame);
        });
    }

    const handleSubmitCreateGame = async (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();

        const response = await fetch("/api/games/create-game", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
        });

        const data = await response.json();
        console.log(data);
        setGame(data);
    };

    const handleSubmitJoinRandomGame = async (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();

        const response = await fetch("/api/games/connect/random", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
        });

        const data = await response.json();
        console.log(data);
        setGame(data);
    }

    const handleSubmitJoinGame = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const response = await fetch(`/api/games/connect/${gameId}`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
        });
        const data = await response.json();
        console.log(data);
        setGame(data);
    };

    const handleSubmitReconnect = async (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();

        const response = await fetch("/api/games/game-reconnect", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
        });

        const data = await response.json();
        console.log(data);
        setGame(data);
    }

    const handleSubmitSurrender = async (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();

        fetch("/api/games/game-surrender", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
        });

    }

    const handleResetGame = (time: number) => {
        setTimeout(() => {
            setGame({gameBoard: "000000000", id: null, status: "NEW", winner: null , currentPlayerTurn: undefined, playerX: "Searching...", playerXColor: "#FFFFFF", playerO: "Searching...", playerOColor: "#FFFFFF"})
            setGameInProgress(false);
        }, time);
    };

    const handleCellClick = async (coordinateX: number, coordinateY: number) => {
        if (game.status === "FINISHED") {
            return;
        }

        const move: Move = {
            coordinateX: coordinateX,
            coordinateY: coordinateY,
        };

        await fetch("/api/games/gameLoop", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("jwt"),
                "Content-Type": "application/json",
            },
            body: JSON.stringify(move),
        });
    };

    const handleLeaveGameWhileReconnecting = (event: React.MouseEvent<HTMLButtonElement>) => {
        handleSubmitSurrender(event);
        handleResetGame(500);
    }

    const renderBoard = (game: Game) => {
        return (
            <div>
                <div>
                    {game.currentPlayerTurn} turn
                </div>
                <div id="board" className="table">
                    {game.gameBoard.split("").map((cell, index) => (
                        <div key={index} className="cell" onClick={() => {
                            const row = Math.floor(index / 3);
                            const col = index % 3;
                            handleCellClick(row, col);
                        }}>
                            {cell === "0" ? "" : (
                                <div style={{ color: cell === "1" ? game.playerXColor : game.playerOColor }}>
                                    {cell === "1" ? "X" : "O"}
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        );
    };

    const renderSurrenderButton = (game: Game) => {
        if (game.id && game.status !== "FINISHED") {
            return (
                <div className="button-container">
                    <button onClick={handleSubmitSurrender}>
                        Leave game
                    </button>
                </div>
            );
        }
    }

    const renderGameOver = (game: Game) => {
        if (!game.winner) {
            return null;
        }
        if (game.winner === "TIE") {
            return (
                <div className="alert">
                    <span>It's a tie!</span>
                </div>
            );
        } else {
            return (
                <div className="alert">
                    {game.winner === "X" ? "Player X" : "Player O"} wins!
                </div>
            );
        }
    };

    const render = () => {
        if (!game.id && gameInProgress) {
            return (
                <div className="button-container">
                    <button onClick={handleLeaveGameWhileReconnecting} className="btn">
                        Leave Game
                    </button>
                    <button onClick={handleSubmitReconnect} className="btn">
                        Reconnect
                    </button>
                </div>
            );
        }
        if (!game.id) {
            return (
                <div className="button-container">
                    <button onClick={handleSubmitCreateGame} className="btn">
                        New Game
                    </button>
                    <form onSubmit={handleSubmitJoinGame} className="form-container">
                        <label htmlFor="gameId">Game ID:</label>
                        <input
                            type="text"
                            id="gameId"
                            value={gameId}
                            onChange={(event) => setGameId(event.target.value)}
                        />
                        <button type="submit" className="btn">Join Game</button>
                    </form>
                    <button onClick={handleSubmitJoinRandomGame} className="btn">
                        Random Game
                    </button>
                </div>
            );
        }
        if (game.status === "FINISHED") {
            {
                handleResetGame(850)
            }
        }
        return (
            <>
                <div className="game-information-container">
                    <div id="game-id">
                        Game ID: {game.id}
                    </div>
                    <div id="playerX">
                        <span>X: </span>
                        <span style={{color: game.playerXColor}}>{game.playerX}</span>
                    </div>
                    <div id="playerO">
                        <span>O: </span>
                        <span style={{color: game.playerOColor}}>{game.playerO}</span>
                    </div>
                </div>
                {game.id && renderBoard(game)}
                {renderSurrenderButton(game)}
                {renderGameOver(game)}
            </>
        );
    };

    return <div className="tic-tac-toe-container">{render()}</div>;
};

export default TicTacToe;
