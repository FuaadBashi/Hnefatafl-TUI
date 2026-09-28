package ws.aperture.hnefatafl.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ws.aperture.hnefatafl.model.enums.Side;

class GameTest {

    private static List<String> movesFrom(Board board, String square) {
        return board.getSquare(square).getPiece().getMoves().stream()
                .map(Square::toString)
                .toList();
    }

    @Test
    void aNewGameStartsWithTheStandardCopenhagenLayout() {
        Game game = new Game("Attacker", "Defender", false);

        assertEquals(Side.ATTACKING, game.getPlayerTurn());
        assertEquals(24, game.getAttackers().size());
        assertEquals(12, game.getDefenders().size(), "defending pawns; the king is tracked apart");
        assertEquals("F6", game.getKingSquare().toString());
    }

    @Test
    void piecesMoveLikeRooksButCannotStopOnACorner() {
        Board board = new Board("F6", new String[] {"F4"}, new String[] {"A3"});

        List<String> moves = movesFrom(board, "A3");

        assertTrue(moves.contains("A2"));
        assertTrue(moves.contains("A10"));
        assertTrue(moves.contains("K3"));
        assertFalse(moves.contains("A1"), "corners are reserved for the king");
        assertFalse(moves.contains("A11"), "corners are reserved for the king");
    }

    @Test
    void theTurnPassesToTheDefenderAfterTheAttackerMoves() {
        Game game = new Game("Attacker", "Defender", false);

        GameDTO state = game.move("D1", "D3");

        assertEquals(Side.DEFENDING, game.getPlayerTurn());
        assertTrue(state.attackers().contains("D3"));
        assertFalse(state.attackers().contains("D1"));
    }

    @Test
    void aPieceSandwichedBetweenTwoEnemiesIsCaptured() {
        Board board = new Board("F6", new String[] {"D3", "F4"}, new String[] {"C3", "E8"});
        Game game = new Game("Attacker", "Defender", board);

        GameDTO state = game.move("E8", "E3");

        assertFalse(state.defenders().contains("D3"));
        assertTrue(state.defenders().contains("F4"));
    }

    @Test
    void theDefenderWinsWhenTheKingReachesACorner() {
        Board board = new Board("C1", new String[] {"F4", "F5"}, new String[] {"H8", "J9"});
        Game game = new Game("Attacker", "Defender", board);

        game.move("H8", "H7");
        GameDTO state = game.move("C1", "A1");

        assertEquals("Defender", state.winner());
    }
}
