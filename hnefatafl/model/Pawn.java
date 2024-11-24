package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.aperture.hnefatafl.model.Board;
import ws.aperture.hnefatafl.model.Square;
import ws.aperture.hnefatafl.model.enums.Direction;
import ws.aperture.hnefatafl.model.enums.Side;

public final class Pawn extends Piece {

   
    
    public Pawn(Side side, Board board, Square square){
        super(side, board, square);
    }

    @Override
    public String toString() {
        return super.toString() + "\u265F";
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
            int[] d = Direction.getDelta( direction );

            for (;;) {

				newRow = currentRow + (steps * d[0]);
				newCol = currentCol + (steps * d[1]);

                if ( !Board.onBoard( newRow, newCol ) || Board.isCornerSquare( newRow, newCol )) {
                    break;
                }

                prospective = board.getSquare( newRow, newCol );
                
                
                if ( Board.isThroneSquare( prospective ) ) {

                    if ( prospective.isEmpty()) {
                        ++steps;
                    } else {
                        break;
                    }
                    
                } else if ( prospective.isEmpty() ) {

                    List<Square> attacks =  calcAttacks( prospective, Direction.opposite(direction) );
                    movesAttacks.put(prospective, attacks);
                    ++steps;
                } else {
                    break;
                }

            }
        }

        return !movesAttacks.isEmpty();
    }

    @Override
    protected List<Square> calcAttacks( Square prospective, Direction fromDirection) {
        List<Square> attacks = new ArrayList<Square>();
        int newRow, newCol;
        Square oneStep, twoStep;

        for (Direction d : Direction.values() ) {

            if ( d == fromDirection) {
                continue;
            }

            
            int[] delta = Direction.getDelta(d);
            int currentRow = prospective.getRow();
            int currentCol = prospective.getCol();

            newRow = currentRow + delta[0];
            newCol = currentCol + delta[1];

            if( !Board.onBoard( newRow, newCol ) ){
                continue;
            }
            oneStep = board.getSquare(newRow, newCol);

            
            
            if ( oneStep.isEmpty() ) {
                continue;
                
            }

            Piece oneStepOccupier = oneStep.getPiece();

            // Opposite sides
            if ( !oneStepOccupier.sameSide( side ) ) {
                
                newRow += delta[0];
                newCol += delta[1];
                
                if( !Board.onBoard(newRow, newCol) ) {
                    continue;
                }

                twoStep = board.getSquare( newRow, newCol );

                if ( Board.isCornerSquare(twoStep) || Board.isThroneSquare(twoStep)) {
                    attacks.add( oneStep );
                    continue;
                }
                
                if ( twoStep.isEmpty() ) {
                    continue;
                }

                Piece twoStepOccupier = twoStep.getPiece();
                
                if( twoStepOccupier.sameSide( side ) ) {
                    // SANDWICH ACHIEVED

                    if ( oneStepOccupier instanceof King) {
                        if (board.kingEncircledOnAllDirectionsBut( Direction.opposite( d ) ) ) {
                            attacks.add( oneStep );
                        }
                    } else {
                        attacks.add( oneStep );
                    }

                    
                } else {
                    // SHIELD WALL CHECK
                    

                    int shieldWallVictims  = board.isShieldWall( oneStep, d );

                    Square victimSquare = oneStep;
                    if (shieldWallVictims != 0) {
                        for (int i = 0; i < shieldWallVictims; ++i) {
                            if ( victimSquare != board.getKingSquare()) {
                                attacks.add( victimSquare );
                            }
                            
                            victimSquare = board.getNeighbour( victimSquare, d);
                        }
                    }
                }
            }
        }
        return attacks;
    }

   



}
