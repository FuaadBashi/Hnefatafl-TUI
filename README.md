# Hnefatafl — Java Terminal Game

A terminal implementation of the asymmetric board game Hnefatafl, with separate board, piece, game-state, and interface components.

## Run locally

Use a modern JDK (17 or later; the source uses records). From a POSIX shell:

```bash
git clone https://github.com/FuaadBashi/Hnefatafl-TUI.git
cd Hnefatafl-TUI
mkdir -p out
find hnefatafl -name '*.java' > sources.txt
javac -d out @sources.txt
java -cp out ws.aperture.hnefatafl.App
```

The start screen collects attacker and defender names and a debug-mode choice.

## Code to explore

- [App.java](hnefatafl/App.java): connects the terminal interface to the game.
- [Board.java](hnefatafl/model/Board.java): board state and move handling.
- [Game.java](hnefatafl/model/Game.java): game coordination.
- [TUI.java](hnefatafl/TUI.java): terminal interaction.
- [GameDTO.java](hnefatafl/model/GameDTO.java): the state passed to the interface.

The source folders differ from the Java package names, so compile with `-d out` and use the fully qualified main class above.
