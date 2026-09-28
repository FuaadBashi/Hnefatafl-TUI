# Hnefatafl TUI

[![CI](https://github.com/FuaadBashi/Hnefatafl-TUI/actions/workflows/ci.yml/badge.svg)](https://github.com/FuaadBashi/Hnefatafl-TUI/actions/workflows/ci.yml)

The Viking board game **Hnefatafl** ("King's Table") played in the terminal, written in Java and
following the Copenhagen rules. It is an asymmetric game: 24 attackers try to capture the king,
while 12 defenders try to get him to a corner of the 11×11 board.

## Highlights

- **Rules engine.** Pieces move like rooks. Only the king may stop on the throne or a corner.
  Pieces are captured by being sandwiched between two enemies or an enemy and a hostile square.
- **Every end condition, not just the obvious ones.** The defenders win if the king escapes to a
  corner or builds an unbreakable *exit fort* on the edge. The attackers win by surrounding the
  king on four sides or by *encircling* the defence, which is detected with a breadth-first search
  for any remaining route to the edge.
- **Separation of model and view.** The terminal UI never touches the board directly. It receives
  immutable `GameDTO` and `MoveDTO` records describing the position and the legal moves.
- **Scenario fixtures.** Positions from the Copenhagen rules diagrams are encoded as `Board`
  factories and used by the tests.

## Getting started

Requires JDK 17+ and Maven.

```bash
git clone https://github.com/FuaadBashi/Hnefatafl-TUI.git
cd Hnefatafl-TUI
mvn package
java -jar target/hnefatafl-tui.jar
```

Enter a name for each side. Press Enter to start a normal game, or type `d` to start from a
prepared mid-game position.

## How to play

| Input | Action |
| --- | --- |
| `d1d3` | Move the piece on D1 to D3 |
| `v`, then a square | Show a piece's legal moves |
| `m` | Enter a move in two steps: source square, then destination |
| `exit` | Quit |

Attackers move first.

## Project structure

```
src/main/java/ws/aperture/hnefatafl/
├── App.java              entry point
├── TUI.java              rendering and input handling
├── model/
│   ├── Game.java         turns and end-of-game checks
│   ├── Board.java        captures, exit forts, encirclement search, scenario fixtures
│   ├── Piece.java        shared piece behaviour; Pawn and King specialise it
│   └── *DTO.java         read-only snapshots passed to the UI
└── utilities/            ANSI colours, Pair
```

## Tests

```bash
mvn verify
```

This runs the JUnit suite and checks formatting with google-java-format. The suite covers the
starting layout, movement restrictions, custodial capture, turn order, and a king escape.
