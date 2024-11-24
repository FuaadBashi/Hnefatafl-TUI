package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.aperture.hnefatafl.model.enums.Side;
import ws.aperture.hnefatafl.model.Piece;

public class Game {


    private Side              turn;
    private Board             board;
    private int               moveCounter;
    private Map<Side, Player> players;
    private Player            winner;
    private boolean           gameOver;




    public Game(String attackerName, String defenderName, boolean debugMode){

        turn = Side.ATTACKING;
        board = (debugMode) ? Board.kingMakesSWAttack() : new Board() ;
        moveCounter = 0;
        gameOver = false;
        winner = null;

        players = new HashMap<Side, Player>();
        players.put(Side.ATTACKING,  new Player(attackerName ));
        players.put(Side.DEFENDING, new Player(defenderName));

    }

    boolean gameOver() {
        return gameOver;
    }

    String getWinner() {
        return winner == null ? "" : winner.getName();
    }

    int getMoveCount() {
        return (moveCounter / 2) + 1;
    }

    List<Square> getMoves( Piece piece ) {
        return piece.getMoves();
        // return board.getMovesAndAttacks( piece ).getFirst();
    }

    List<Square> getAttacks( Piece piece ) {
        
        return piece.getAttacks();
        // return board.getMovesAndAttacks( piece ).getSecond();
    }

    Square getSquare( int row, int col ) {
        return board.getSquare( row, col );
    }

    /*  Assumes square code has already been validated by the TUI,
        no NULL checking on purpose, if we get a NULL here we
        want to see it.  */ 
    Square getSquare( String squareCode ) {
        return board.getSquare(squareCode);
    }

    public GameDTO move(String sourceloc, String destLoc) {

        if ( !gameOver() ) {

            Piece piece = board.getSquare(sourceloc).getPiece();
            Square destSquare = board.getSquare(destLoc);

            board.move( piece, destSquare );
            endTurn();
        }
        
        return GameDTO.generateDTO(this);
    }

    String getPlayerName(Side side) {
        return players.get(side).getName();
    }

    int getKingSurroundedSides() {
        return board.kingSurroundedSides();
    }

    int getNumPieces(Side side) {
        return board.getNumPieces(side);
    }

    List<Piece> getPieces(Side side) {
        return board.getPieces(side);
    }

    Square getKingSquare() {
        return board.getKingSquare();
    }

    List<Piece> getDefenders() {
        return board.getDefenders();
    }

    List<Piece> getAttackers() {
        return board.getAttackers();
    }


    private void endTurn() {
        
        if (checkEndGame()) {
            return;
        }

        boolean movesAvailable = board.calcMovesAttacks( Side.otherSide(turn) ); 



        if (!movesAvailable) {
            winner = players.get( turn );
            gameOver = true;
            return;
        }

        switchPlayerTurn();
        moveCounter++;
    }


/** TODO:
 *      Need to add encirclement check here
 * @return
 */
    private boolean checkEndGame() {
        if ( getNumPieces(Side.ATTACKING) == 0 || board.kingEscaped()  || board.checkExitFort() ) {
            winner = players.get( Side.DEFENDING );
            gameOver = true;
            return true;
        }

        if ( board.kingCaptured() || board.kingAloneAgainstWall() || board.noRouteToExit()) {
            winner = players.get( Side.ATTACKING );
            gameOver = true;
            return true;
        }

        return false;
    }

    void switchPlayerTurn() {
        turn = Side.otherSide( turn );
    }
    
    Side getSide(Piece piece){
        return piece.getSide();
    }
    public static int getNumRowsCols() {
        return Board.NUM_ROW_COL;
    }

    Side getPlayerTurn() {
        return turn;
    }

}
