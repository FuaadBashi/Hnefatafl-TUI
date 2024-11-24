package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ws.aperture.hnefatafl.model.Board;
import ws.aperture.hnefatafl.model.Square;
import ws.aperture.hnefatafl.model.enums.Direction;
import ws.aperture.hnefatafl.model.enums.Side;

public class King extends Piece {

    final int[][] deltas = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } }; // N, E, S, W

    public King(Board board, Square square) {
        super(Side.DEFENDING, board, square);
    }

    @Override
    public String toString() {
        return super.toString() + "\u2655";
    }

    @Override
    public boolean calcMovesAttacks() {
        movesAttacks = new HashMap<Square, List<Square>>();

        final int currentRow = square.getRow();
        final int currentCol = square.getCol();

        int newRow, newCol;
        Square prospective;

        for (Direction direction : Direction.values()) {
            int steps = 1;
            int[] d = Direction.getDelta(direction);

            for (;;) {

                newRow = currentRow + (steps * d[0]);
                newCol = currentCol + (steps * d[1]);

                if (!Board.onBoard(newRow, newCol)) {
                    break;
                }

                prospective = board.getSquare(newRow, newCol);

                if (Board.isThroneSquare(prospective)) {

                    if (prospective.isEmpty()) {
                        List<Square> attacks = new ArrayList<Square>();
                        attacks.addAll(calcAttacks(prospective, Direction.opposite(direction)));
                        movesAttacks.put(prospective, attacks);
                        ++steps;

                    } else {
                        break;
                    }

                } else if (prospective.isEmpty()) {

                    List<Square> attacks = new ArrayList<Square>();
                    attacks.addAll(calcAttacks(prospective, Direction.opposite(direction)));

                    movesAttacks.put(prospective, attacks);
                    ++steps;
                } else {
                    break;
                }

            }
        }

        return !movesAttacks.isEmpty();
    }

    /**
     *  @param prospective      The prospective Square to which we are considering moving.
     *  @param fromDirection    The direction which we moved from to reach prospective.
     * 
     *      Out of the 4 squares that are adjacent to the square which we are moving to (prospective),
     *      we want to check each for an enemy, then determine if a friendly soldier is one step further.
     * 
     *      One of the adjacent sqaures will be the direction we came from to reach this square, this is
     *      the fromDirection, which we can skip over when checking each possible adjacent square.
     */
    protected List<Square> calcAttacks(Square prospective, Direction fromDirection) {
        List<Square> attacks = new ArrayList<Square>();
        int newRow, newCol;
        Square oneStep, twoStep;

        for (Direction d : Direction.values()) {

            if (d == fromDirection) {
                continue;
            }

            int[] delta = Direction.getDelta(d);
            int currentRow = prospective.getRow();
            int currentCol = prospective.getCol();

            newRow = currentRow + delta[0];
            newCol = currentCol + delta[1];

            if (!Board.onBoard(newRow, newCol)) {
                continue;
            }
            oneStep = board.getSquare(newRow, newCol);

            if (oneStep.isEmpty()) {
                continue;
            }

            Piece oneStepOccupier = oneStep.getPiece();

            // Opposite sides
            if (!oneStepOccupier.sameSide(side)) {

                newRow += delta[0];
                newCol += delta[1];

                if (!Board.onBoard(newRow, newCol)) {
                    continue;
                }

                twoStep = board.getSquare(newRow, newCol);
                if ( Board.isCornerSquare(twoStep) || Board.isThroneSquare(twoStep) ) {
                    attacks.add( oneStep );
                    continue;
                }
                

                if (twoStep.isEmpty()) {
                    continue;
                }

                Piece twoStepOccupier = twoStep.getPiece();

                if (twoStepOccupier.sameSide(side)) {
                    // SANDWICH ACHIEVED
                    attacks.add(oneStep);
                } else {
                    // SHIELD WALL CHECK


                    int sheildWallwVitims = board.isShieldWall(oneStep, d);

                    Square victimSquare = oneStep;
                    if( sheildWallwVitims != 0 ) {
                        for(int i = 0; i<sheildWallwVitims; ++i) {
                            attacks.add(victimSquare);
                            victimSquare = board.getNeighbour(victimSquare, d);
                        }
                    }
                
                }
            }
        }
        return attacks;
    }

}
