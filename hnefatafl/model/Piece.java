package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ws.aperture.hnefatafl.model.Board;
import ws.aperture.hnefatafl.model.Square;
import ws.aperture.hnefatafl.model.enums.Direction;
import ws.aperture.hnefatafl.model.enums.Side;
public abstract class Piece {
    protected final Side side;
    protected final Board board;

    protected Square square;
    protected Map<Square, List<Square>> movesAttacks;
    

    
    protected Piece(Side side, Board board, Square square) {
        this.side = side;
        this.board = board;
        this.square = square;
    }

    /**
     *  Implemented with minimal difference in Pawn vs King
     * 
     *  Returns true if this piece has moves available, false otherwise
     *  Used to determine if player has any moves available
     */
    public abstract boolean calcMovesAttacks();

    public void resetMovesAttacks() {
        movesAttacks = new HashMap<Square, List<Square>>();
    }


    /**     
     *      If this piece moves to the square moveTo, this function
     *      returns all squares that will be `attacked` by moving to
     *      this square.
     * 
     * @param moveTo      The square to which this piece is considering to move to.
     * @return            The List of Squares that will be attacked if this piece makes said move.
     */
    public List<Square> getAttacks( Square moveTo ) {
        return movesAttacks.get(moveTo);
    }


    /**
     * @return      All distinct squares that are under attack from this piece,
     *              from the set of all moves this piece can currently make.
     */
    public List<Square> getAttacks() {
        Set<Square> attackSet = new HashSet<Square>();

        for ( List<Square> attackList : movesAttacks.values() ) {
            for ( Square attack : attackList ) {
                attackSet.add(attack);
            }
        }

        return new ArrayList<Square>( attackSet );
    }


    
    protected abstract List<Square> calcAttacks( Square prospectiveMove, Direction fromDirection );    

    public List<Square> getMoves() {
        return new ArrayList<Square>( movesAttacks.keySet() );
    }
   

    public Board getBoard() {
        return board;
    }

    public Side getSide() {
        return side;
    }

    public int getRow() {
        return square.getRow();
    }

    public int getCol() {
        return square.getCol();
    }

    public char getColChar() {
        return square.getColChar();
    }

    public Square getSquare() {
        return square;
    }

    public static boolean sameSide( Piece p1, Piece p2) {
        return p1.getSide() == p2.getSide();
    }

    public boolean sameSide(Piece otherPiece) {
        return side == otherPiece.getSide();
    }

    public boolean sameSide(Side side) {
        return this.side == side;
    }

    public void setSquare(Square square) {
        this.square = square;
    }


    @Override
    public  String toString(){
        return "";
    }
}
