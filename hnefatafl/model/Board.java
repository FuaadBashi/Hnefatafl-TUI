package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ws.aperture.hnefatafl.TUI;
import ws.aperture.hnefatafl.model.enums.Direction;
import ws.aperture.hnefatafl.model.enums.Side;
import ws.aperture.hnefatafl.model.King;
import ws.aperture.hnefatafl.model.Pawn;
import ws.aperture.hnefatafl.model.Piece;
import ws.aperture.hnefatafl.utilities.Pair;

public class Board {
    static final int NUM_ROW_COL = 11;
    private static final int [] THRONE = {5,5};
    private static final int [][] CORNERS = {{0,0}, {0,10}, {10,0}, {10,10}};
    private static final int STARTER_ATTACKER_COUNT = 24;
    private static final int STARTER_DEFENDER_COUNT = 12;
    private static final String[] ATTACKER_STARTS, DEFENDER_STARTS;

    static {
        ATTACKER_STARTS = new String[] {
            "A4", "A5", "A6", "A7", "A8", "B6",
            "D11", "E11", "F11", "G11", "H11", "F10",
            "D1", "E1", "F1", "G1", "H1", "F2",
            "K4", "K5", "K6", "K7", "K8", "J6",
        };

        DEFENDER_STARTS = new String[] {
            "D6",
            "E5", "E6", "E7",
            "F4", "F5", "F7", "F8",
            "G5", "G6", "G7",
            "H6",
           
        };

    }

    private int attackerCount, defenderCount;
    private final Square[][] board;
    private final List<Piece> attackerPieces, defenderPieces;
    private Square kingSquare;


    /**
     *  Used for testing, allows setup of an incorrect board, with correct number of pieces.
     *  Pawns still obey restrcited square rules.
     * 
     *  To ensure the constructor does not throw:
     *  - If kingCode is bad, does not create
     *  - If any attacker code is bad, no attackers are created
     *  - If any defender code is bad, no defenders are created
     */

    Board( String setKingSquare, String[] defenderSquares, String[] attackerSquares) {

        board = new Square[NUM_ROW_COL][NUM_ROW_COL];
        attackerPieces = new ArrayList<Piece>();
        defenderPieces = new ArrayList<Piece>();
        
        createNewEmptyBoard();


        if ( squareCodeOK(setKingSquare)) {
            kingSquare = getSquare( setKingSquare );
            Piece king = new King( this, kingSquare );
            placePiece( king, kingSquare );
            defenderPieces.add(king);
        }

        populateFromArray(defenderSquares, Side.DEFENDING);
        populateFromArray(attackerSquares, Side.ATTACKING);

        calcMovesAttacks(Side.ATTACKING);
    }

     Board( String setKingSquare ) {
        this( setKingSquare, DEFENDER_STARTS, ATTACKER_STARTS);
    }

     Board() {
        this( "F6");
    }

     static Board debugBoard1() {
        return new Board( "B1", new String[] {"H6"}, ATTACKER_STARTS);
    }


    /**
     * https://aagenielsen.dk/copenhagenregler/omringet.png
     */
     static Board circleEx1() {
        return new Board("F5", 
            new String[] { 
                "D4", "D6", "D7", "D8",
                "E5", "E9",
                "F3", /* F5,*/ "F8",
                "G4", "G7",
                "H6"
            },
            new String[] {
                "A6", "A7",
                "B4", "B5", "B8",
                "C3", "C9",
                "D2", "D10",
                "E2", "E10",
                "F2", "F10",
                /* should be G1 not H1 */"H1", "G9",
                "H2", "H9",
                "I3", "I6", "I7", "I8",
                "J4", "J5"
            }); 
    }

    /**
     * https://aagenielsen.dk/copenhagenregler/omringet.png
     */
     static Board shieldWallEx1() {
        return new Board("F6", 
            new String[] { 
                "J2", "J3", "I4", "J5", "J6", "K7" 
            },
            new String[] {
                "K2", "K3", "K5", "K6", "A2"
            }); 
    }

     static Board shieldWallEx2() {
        return new Board("F6", 
            new String[] { 
                "J4", "J5", "I6", "J7", "J8", "K9", "K2"
            },
            new String[] {
                "K4","K5", "K7", "K8","A2"
            }); 
    }

    /**
     * 
     * Page 2 FB example
     * 
     */

     static Board shieldWallEx3() {
        return new Board("F6", 
            new String[] { 
                "J6", "J7", "J8", "J9", "I10", "K5"
            },
            new String[] {
                "K6","K7", "K8", "K9", "A2"
            }); 
    }

    /*
     * 
     *  Page 5i - King not taken
     */

      static Board shieldWallEx4() {
        return new Board("K7", 
            new String[] { 
                "K6", "K8", "A4"
            },
            new String[] {
                "K5", "J6", "J7", "J8", "J9", "A2"
            }); 
    }

  
    /*
     * 
     *  Page 5ii - King not taken, but exit opened
     */
  
     static Board shieldWallEx5() {
        return new Board("K3", 
            new String[] { 
                "K2",
            },
            new String[] {
                "J2", "J3", "K6"
            }); 
    }



     static Board exitFortEx1() {
        return new Board("E1", 
            new String[] { 
                "D1", "E2", "F2", "H1", "K9"
            },
            new String[] {
                "K8"
            }); 
    }

    static Board exitFortEx2() {
        return new Board("F1", 
            new String[] { 
                "E1", "E2", "E3", "F3", "G2", "I1"
            },
            new String[] {
                "K8"
            }); 
    }


    static Board kingMakesSWAttack() {
        return new Board("H3", 
            new String[] { 
                "J7", "K9", "J8", "J4", "I5", "I6"
            },
            ATTACKER_STARTS ); 
    }


    private void createNewEmptyBoard() {
        for (int i = 0; i < NUM_ROW_COL; i++) {
            for (int j = 0; j < NUM_ROW_COL; j++) {
                board[i][j] = new Square(i, j);
            }
        }
    }

    /** 
     * @param squareCode        User input representing a sqaure on the Hnefatafl board, e.g. "A10", "B3", "a2", "c6" 
     * @return                  True    if provided squareCode (case-insensitive) maps to a square on the 11x11 HNEFATAFL board
     *                          False   otherwise         
     */
    static boolean squareCodeOK( String squareCode ) {
        if ( squareCode == null || squareCode.isEmpty()) {
            return false;
        }


        if ( squareCode.length() < 2 || squareCode.length() > 3) {
            return false;
        }

        char col = Character.toUpperCase(squareCode.charAt(0));

        if ( col < 'A' || col > 'K' ) {
            return false;
        }

        String rowStr = squareCode.substring(1);
        int row;
        try {
            row = Integer.parseInt( rowStr );
        } catch (NumberFormatException nfe) {
            return false;
        }

        if ( row < 1 || row > 11 ) {
            return false;
        }

        return true;

    }

    static boolean badSquareCode( String squareCode ) {
        return !squareCodeOK(squareCode);
    }

    int getNumPieces( Side side ) {
        return side == Side.ATTACKING ? attackerCount : defenderCount;
    }

    int kingSurroundedSides() {
        int encircledDirections = 0;
        for ( Direction d : Direction.values() ) {
            int[] delta = Direction.getDelta(d);
            int kingRow = kingSquare.getRow();
            int KingCol = kingSquare.getCol();

            int newRow = kingRow + delta[0];
            int newCol = KingCol + delta[1];

            if ( !Board.onBoard(newRow, newCol) ) {
                continue;
            }

            Square adjSquare = getSquare(newRow, newCol);

            if ( (adjSquare.isOccupied() && adjSquare.getPiece().sameSide( Side.ATTACKING )) || isThroneSquare(adjSquare)) {
                ++encircledDirections;
            }
        }

        return encircledDirections;
    }


    boolean checkExitFort() {
        if ( ! Board.onEdge(kingSquare) ) {
            return false;
        }

        Direction outer = Direction.getOuter( kingSquare.getRow(), kingSquare.getCol() );
        
        boolean canMove = false;
        for ( Direction direction : Direction.values() ) {
			if ( direction == outer ) {
                continue;
            }

            Square currNeighbour = getNeighbour(kingSquare, direction);

            if ( currNeighbour.isEmpty() ) {
                canMove = true;
                break;
            }
        }

        if (!canMove) {
            return false;
        }


         /*
         *  BFS data structures initialisation
         */
        List<Square> frontier = new ArrayList<Square>();
        Set<Square> exploredSet = new HashSet<Square>();

        // Set of squares for defender pieces minus the King
        Set<Square> defenderSquares = new HashSet<Square>(
            defenderPieces.stream()
            .map( p -> p.getSquare() )
            .filter( sq -> sq != kingSquare )
            .collect( Collectors.toList() ) 
        );

        // If we find an attacker, we are not in an exit fort
        Set<Square> attackerSquares = new HashSet<Square>(
            attackerPieces.stream()
            .map( p -> p.getSquare() )
            .collect( Collectors.toList() ) 
        );

        // If we find an attacker, we are not in an exit fort
        Set<Square> allPieceSquares = new HashSet<Square>();
        allPieceSquares.addAll(attackerSquares);
        allPieceSquares.addAll(defenderSquares);
        allPieceSquares.add(kingSquare);



        frontier.add(kingSquare);

        while ( ! frontier.isEmpty() ) {
            Square currentSquare = frontier.get(0);
            exploredSet.add( currentSquare );
            frontier.remove(0);

            if ( attackerSquares.contains( currentSquare ) ) {
                return false;
            }

            if ( defenderSquares.contains( currentSquare ) ) {                
                continue;
            }


            // BFS: Expand children and append to frontier
            for (Direction direction : Direction.values()) {
                int[] d = Direction.getDelta( direction );
                int newRow = currentSquare.getRow() + d[0];
				int newCol = currentSquare.getCol() + d[1];

                if ( !Board.onBoard(newRow, newCol) ) {
                    continue;
                }

                Square prospectiveSquare = getSquare(newRow, newCol);

                if (  exploredSet.contains(prospectiveSquare) || allPieceSquares.contains(prospectiveSquare) ) {
                    continue;
                }                

                if ( isRestrictedSquare(currentSquare) ) {
                    return false;
                }

                frontier.add( prospectiveSquare );
            }

        }

        return true;

        
    }


    boolean noRouteToExit() {

        /*
         *  BFS data structures initialisation
         */
        List<Square> frontier = new ArrayList<Square>();
        Set<Square> exploredSet = new HashSet<Square>();

        // Set of Squares on which we cannot step
        Set<Square> noStep = new HashSet<Square>(
            attackerPieces.stream()
            .map( p -> p.getSquare() )
            .collect( Collectors.toList() ) 
        );

        // Set of squares for defender pieces minus the King
        Set<Square> defenderSquares = new HashSet<Square>(
            defenderPieces.stream()
            .map( p -> p.getSquare() )
            .filter( sq -> sq != kingSquare)
            .collect( Collectors.toList() ) 
        );



        frontier.add( kingSquare );

        /* BFS Iteration */
        while ( !frontier.isEmpty() ) {

            Square currentSquare = frontier.get(0);


            // if ( exploredSet.contains( currentSquare ) ) {
            //     frontier.remove(0);
            //     continue;
            // }


            exploredSet.add(currentSquare);
            frontier.remove(0);

            int row = currentSquare.getRow();
            int col = currentSquare.getCol();
            
            // Corner square is accessible -> can escape
            if ( Board.isCornerSquare(row, col) ) {
                return false;
            }

            if ( noStep.contains( currentSquare ) ) {
                    continue;
            }

            // must account for all defenders inside the `circle`
            if ( defenderSquares.contains( currentSquare ) ) {
                defenderSquares.remove( currentSquare );
                continue;
            }


            // BFS: Expand children and append to frontier
            for (Direction direction : Direction.values()) {
                int[] d = Direction.getDelta( direction );
                int newRow = currentSquare.getRow() + d[0];
				int newCol = currentSquare.getCol() + d[1];

                if ( !Board.onBoard(newRow, newCol) ) {
                    continue;
                }

                Square prospectiveSquare = getSquare(newRow, newCol);

                if ( exploredSet.contains(prospectiveSquare) ) {
                    continue;
                }
                frontier.add( prospectiveSquare );
            }
        }

        return defenderSquares.isEmpty();

    }

     static boolean onEdge( Square square ) {
        final int row = square.getRow();
        final int col = square.getCol();
        return onEdge( row, col);
    }
    
     static boolean onEdge( int row, int col) {
        for ( int edge : new int[] {0,10}) {
            if ( row == edge || col == edge ) {
                return true;
            }
        }
        
        return false;
    }

     boolean kingAloneAgainstWall() {
        if (defenderCount != 0 ) {
            return false;
        }

        if ( !onEdge( kingSquare) ) {
            return false;
        }

        return kingSurroundedSides() == 3;
    }

     boolean kingEncircledOnAllDirectionsBut( Direction empty ) {
        int encircledDirections = 0;
        for ( Direction d : Direction.values() ) {
            if (d == empty) {
                continue;
            }

            int[] delta = Direction.getDelta(d);
            int kingRow = kingSquare.getRow();
            int KingCol = kingSquare.getCol();

            int newRow = kingRow + delta[0];
            int newCol = KingCol + delta[1];

            if ( !Board.onBoard(newRow, newCol) ) {
                return false;
            }

            Square adjSquare = getSquare(newRow, newCol);


            if ( (adjSquare.isOccupied() && adjSquare.getPiece().getSide() == Side.ATTACKING) || isThroneSquare(adjSquare)) {
                ++encircledDirections;
            }
        }

        return encircledDirections == 3;
    }

    private void populateFromArray( String[] pawns, Side side) {

        final int total = (side == Side.ATTACKING) ? 24 : 12;

        // Exists
        if ( pawns == null ) {
            return;
        }

        // Correct number of attackers
        // if (pawns.length != total) {
        //     return;
        // }

        // No duplicates
        Set<String> pawnSet = new HashSet<String>( Arrays.asList(pawns) );
        if ( pawnSet.size() != pawns.length ) {
            return;
        }

        // Check each input on Board
        for ( String locationString : pawns) {
            if ( badSquareCode(locationString)) {
                return;
            }
        }

        // Check square code maps to non-hostile square
        for ( String locationString : pawns) {
            Square pieceSquare = getSquare(locationString);
            if (Board.isRestrictedSquare(pieceSquare) || pieceSquare == kingSquare) {
                return;
            }
        }

        // Populate
        for ( String locationString : pawns) {
            Square pieceSquare = getSquare( locationString );
            Piece pawn = new Pawn( side, this, pieceSquare );
            placePiece( pawn, pieceSquare );
            if ( side == Side.ATTACKING ) {
                attackerPieces.add( pawn );
                ++attackerCount;
            } else {
                defenderPieces.add( pawn );
                ++defenderCount;
            }
            
        }
    }

    /*
     * 
     * K6[5,10], LEFT -> J6 [5, 9]
     * K5 -> [5, 9], RIGHT -> K6 [5, 10]
     * 
     */
     Square getNeighbour( Square square, Direction d) {
        int row = square.getRow(); // 5
        int col = square.getCol(); // 10

        int[] delta = Direction.getDelta(d); // [-1 , 0]

        row += delta[0]; // 4
        col += delta[1]; // 10

        return getSquare(row, col);

    }

    private boolean placePiece( Piece piece, Square square) {
        int row = square.getRow();
        int col = square.getCol();

        return placePiece(piece, row, col);
    }


    private boolean placePiece(Piece piece, int row, int col) {
        if (onBoard(row, col)) {
            Square destSquare = getSquare(row, col);
            if (piece instanceof King) {
                kingSquare = destSquare;
            }
            
            return destSquare.placePiece(piece);
        } else {
            return false;
        }

    }

    void attack( Square attackedSquare ) {
        Piece victim = attackedSquare.getPiece();

        if ( victim.getSide() == Side.ATTACKING ) {
            attackerPieces.remove(victim);
            --attackerCount;
        } else {

            if ( victim instanceof Pawn ) {
                --defenderCount;
            } 
            defenderPieces.remove(victim);
            
        }

        if ( victim instanceof Pawn ) {
            attackedSquare.removePiece();
        }
    }



     int isShieldWall( Square firstAttackee, Direction d ) {
        if ( !Board.onEdge( firstAttackee )) {
            return -1;
        }

        Direction innerDirection = Direction.getInner( firstAttackee.getRow(), firstAttackee.getCol() );


        // System.out.println( "innerDirection: " + innerDirection );
        Side victimSide = firstAttackee.getPiece().getSide();

        // System.out.println( "victimSide: " + victimSide );

        int victimCount = 0;
        Square currSquare = firstAttackee;

        while (true) {
            
            Square inner = getNeighbour(currSquare, innerDirection);
            // System.out.println( "innerDirection: " + innerDirection );

            // System.out.println( "currSq: " + currSquare );
            // System.out.println( "innerSq: " + inner );

            // Square test = getSquare("J6");

            // System.out.println( "currSq: " + currSquare );
            // System.out.println( "innerSq: " + inner );
            // System.out.println( "test: " + test );

            

            // System.out.println( "currSq(r,c): " + currSquare.getRow() + " " + currSquare.getCol() );
            // System.out.println( "innerSq(r,c): " + inner.getRow() + " " +  inner.getCol() );
            // System.out.println( "test(r,c): " + test.getRow() + " " +  test.getCol() );

            if ( currSquare.isEmpty() ) {
                if ( isCornerSquare(currSquare)) {
                    return victimCount;
                } else {
                    return 0;
                }
            }

            if ( inner.isEmpty()) {
                if ((!currSquare.getPiece().sameSide(victimSide)) && (victimCount > 1)) {
                    return victimCount;
                } else {
                    return 0;
                }
            }

                
            if ( !Piece.sameSide(currSquare.getPiece(), inner.getPiece()) ) {
                ++victimCount;
                currSquare = getNeighbour(currSquare, d);
            }
        }
    }
   

    void move(Piece piece, Square destSquare) {

        Square sourceSquare = piece.getSquare();

        List<Square> attacks = piece.getAttacks(destSquare);

        for ( Square victimSquare : attacks ) {
            attack( victimSquare );
        }

        sourceSquare.removePiece();

        placePiece(piece, destSquare.getRow(), destSquare.getCol()); 
    }


     boolean kingCaptured() {
        return kingSurroundedSides() == 4;
    }

     boolean kingEscaped() {
        return isCornerSquare(kingSquare);
    }

    boolean calcMovesAttacks( Side side  ) {
        boolean movesAvailable = false;
        for (Piece friendly : getPieces( side ) ) {
            movesAvailable |= friendly.calcMovesAttacks();
        }


        for ( Piece enemy : getPieces(Side.otherSide(side))) {
            enemy.resetMovesAttacks();
        }

        return movesAvailable;
    }

    List<Piece> getPieces( Side side ) {
        return side == Side.ATTACKING ? attackerPieces : defenderPieces; 
    }

    List<Piece> getAttackers() {
        return attackerPieces;
    }

    List<Piece> getDefenders() {
        return defenderPieces.stream()
                .filter( p -> ( p instanceof Pawn) )
                .collect( Collectors.toList());
    }

    

     Square getKingSquare() {
        return kingSquare;
    }

     static boolean isHostileSquare(int row, int col) {
        return isCornerSquare(row, col) || isThroneSquare(row, col);
    }

     static boolean isRestrictedSquare(Square square) {
        int row = square.getRow();
        int col = square.getCol();

        return isHostileSquare(row, col);
    }


     static boolean isCornerSquare(int row, int col) {

        for (int [] corner: CORNERS ){
            if (row == corner[0] && col == corner[1]){
                return true;
            }
        }

        return false;
    }

     static boolean isCornerSquare(Square square) {
        int row = square.getRow();
        int col = square.getCol();

        return isCornerSquare(row, col);
    } 

     static boolean isThroneSquare(int row, int col) {
        return row == THRONE[0]
            && col == THRONE[1];
    }

     static boolean isThroneSquare( Square square ) {
        int row = square.getRow();
        int col = square.getCol();

        return isThroneSquare(row, col);
    }


     static boolean onBoard(int row, int col) {
        return (row >= 0 && row < NUM_ROW_COL && col >= 0 && col < NUM_ROW_COL);
    }

     Square getSquare(int row, int col) {
        return onBoard(row, col) ? board[row][col] : null;
    }


    /**
     *  
     * @param squareCode    An uppercased squareCode mapping to the board, e.g. "H4",
     * @return
     */
     Square getSquare( String squareCode ) {
        squareCode = squareCode.toUpperCase();

        int row = Integer.parseInt(squareCode.substring(1)) - 1;
        int col = squareCode.charAt(0) - 'A';

       return getSquare(row, col);
    }
}