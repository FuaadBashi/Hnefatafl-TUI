# Hnefatafl Game

This project implements the classic board game Hnefatafl (often referred to as "The King's Table"). The game is known for its asymmetric gameplay, where one player defends the King, and the other tries to capture him.

## Project Structure

### Main Components

- **`App.java`**: The main entry point of the application.
- **`TUI.java`**: Implements a text-based user interface for interacting with the game.
- **`Args.java`**: Handles command-line arguments for the application.

### Packages and Classes

#### `utilities`
- **`Pair.java`**: A utility class for managing pairs of related objects.
- **`ANSIColour.java`**: Provides utilities for ANSI color codes to enhance terminal output.

#### `model`
- **`Board.java`**: Represents the game board and its state.
- **`Game.java`**: Manages the core game logic.
- **`King.java`**: Defines the King's behavior and properties.
- **`Pawn.java`**: Defines the Pawn's behavior and properties.
- **`Piece.java`**: A superclass for all game pieces.
- **`Square.java`**: Represents individual squares on the board.
- **`Player.java`**: Manages player-related logic.
- **`MoveDTO.java`**: Data Transfer Object for encapsulating move data.
- **`GameDTO.java`**: Data Transfer Object for sharing game state information.
- **`DestResultDTO.java`**: Data Transfer Object for destination-related results.

#### `model.enums`
- **`Side.java`**: Enumerates the sides (e.g., Attackers and Defenders).
- **`Direction.java`**: Enumerates possible movement directions.

## How to Run

1. Ensure you have **Java 8+** installed.
2. Compile the project using a Java compiler:
   ```bash
   javac -d out $(find ./hnefatafl -name "*.java")
