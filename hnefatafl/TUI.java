package ws.aperture.hnefatafl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ws.aperture.hnefatafl.utilities.ANSIColour;
import ws.aperture.hnefatafl.utilities.Pair;
import ws.aperture.hnefatafl.model.DestResultDTO;
import ws.aperture.hnefatafl.model.Game;
import ws.aperture.hnefatafl.model.GameDTO;
import ws.aperture.hnefatafl.model.MoveDTO;
import ws.aperture.hnefatafl.model.Piece;
import ws.aperture.hnefatafl.model.Player;
import ws.aperture.hnefatafl.model.Square;
import ws.aperture.hnefatafl.model.enums.Side;

public class TUI {
    
    private Game game;
    private GameDTO gameDTO;
    private Scanner scanner;
    private String  message;
    private char mode;
    private int promptIndex;
    

    private String  sourceLocation, destLocation;

    private final static ArrayList<String> promptMessages;

    static {
        promptMessages = new ArrayList<String>();
        promptMessages.add("      (V)iew moves and attacks    (M)ove piece       ");    // 0
        promptMessages.add("      Enter location of piece:    ");                       // 1
        promptMessages.add("      Enter destination:    "); //2
        promptMessages.add("      Invalid Location.     "); // 3
        promptMessages.add(  "    Moves are highlighted "
                + ANSIColour.ANSI_GREEN_BACKGROUND + "green" + ANSIColour.ANSI_RESET 
                + ", attacks are highlighted "
                + ANSIColour.ANSI_RED_BACKGROUND + "red" + ANSIColour.ANSI_RESET
                + ".    "); //4


        promptMessages.add("      No moves available"); //5
        promptMessages.add("      Invalid destination."); //6
        promptMessages.add("      Friendly fire will not be tolerated!");//7
        promptMessages.add("      Not your turn."); //8
        promptMessages.add("      Invalid piece code"); //9
        promptMessages.add("      Bad move code"); //10
    }
   
    public TUI(){
        scanner = new Scanner(System.in);
        promptIndex = 0;
        mode = 's'; /* s : start, v : view, m : move (source stage), d : move (destination stage) */
    }

    private String getPawnString() {
       return "\u265F";
    }

    private String getKingString() {
        return "\u2655";
     }

    /**
     *  Gets user input for names, creates Players and the Game.
     */
    Args startScreen() {
        clearScreen();
        String attackerName, defenderName, specialMode;

        System.out.println("Enter Attacker player name: ");
        attackerName = scanner.nextLine();

        System.out.println("Enter Defender player name: ");
        defenderName = scanner.nextLine();

        System.out.println("Enter d for debug mode, otherwise enter");
        specialMode = scanner.nextLine();


        boolean debugMode = specialMode.toUpperCase().equals("D");

        // game = new Game(attackerName, defenderName, debugMode);
        
        // gameDTO = GameDTO.generateDTO(game);

        return new Args(attackerName, defenderName, debugMode);

    }

    private void doScreenPrompt(int setIndex) {
        promptIndex = setIndex;
        drawBoard2();
        try {
            TimeUnit.SECONDS.sleep( 2 );
        } catch ( InterruptedException ie ) {
            System.out.println( "Clock interrupted" );
        }
        promptIndex = 0;
        mode = 's';
    }

    private void doViewMode() {

        String squareCode = message;

        if ( badSquareCode(squareCode) ) {
            doScreenPrompt(3);
            return;
        }

        for( MoveDTO md: gameDTO.moves()){

            if (md.sourceLocation().equals(squareCode)){

                if( ! (md.destinationPairs().isEmpty()) ){

                    promptIndex = 0;
                    drawBoard2( squareCode );

                    try {
                        TimeUnit.SECONDS.sleep( 3 );
                    } catch ( InterruptedException ie ) {
                        System.out.println( "Clock interrupted" );
                    }
                    promptIndex = 0;
                    mode = 's';
                    return;

                } else {
                    doScreenPrompt(5);
                    return;
                }
            } 
        } 

        if (isOccupied()){

            doScreenPrompt(8);
            return;

        } else {

            doScreenPrompt(3);
            return;
        }

    }



    private void shortMoveMode() {
        if ( message.length() < 4 || message.length() > 6 ) {
            doScreenPrompt( 10 );
            return;
        }


        Pair<String, String> splitMessages;
        try {
            splitMessages = splitSrcDest(message);
        } catch ( IllegalArgumentException iae ) {
            doScreenPrompt( 10 );
            return;
        }
            
        sourceLocation = splitMessages.getFirst();
        destLocation   = splitMessages.getSecond();

        /* Check source square */
        if ( badSquareCode( sourceLocation ) ) {
            doScreenPrompt(3);
            return;
        }

        /* Check dest square */
        if ( badSquareCode( destLocation ) ) {
            doScreenPrompt(6);
            return;
        }


        /* No Piece on Source Square */
        if ( isOccupied() ) {
            doScreenPrompt(3);
            return;
        }

        
        if ( gameDTO.turn().equals("ATTACKING") && (!gameDTO.attackers().contains(sourceLocation))){
                    
            doScreenPrompt(8);
            return;
        }

        if ( gameDTO.turn().equals("DEFENDERS") 
            && ( !gameDTO.defenders().contains(sourceLocation) && !gameDTO.kingSquare().equals(sourceLocation) ) ) {
            
            doScreenPrompt(8);
            return;  
        }
            

        for (MoveDTO md: gameDTO.moves()){
            
            if (md.sourceLocation().equals(sourceLocation)){
                for (DestResultDTO drd: md.destinationPairs()){
                    if (drd.destLocation().equals(destLocation)){
                        this.gameDTO = game.move(sourceLocation, destLocation);
                        return;
                    } 
                } 

                doScreenPrompt(6);
            }
        }
    }
    


    public void start(GameDTO gameDTO, Game game){
        // startScreen();
        this.gameDTO = gameDTO;
        this.game = game;

        do {
            drawBoard2();
            message = scanner.next().toUpperCase();
            if ( mode == 's' ) {
                promptIndex = 0;
                
                if ( message.equals("EXIT") ) {
                    break;
                }

                else if (   message.equals("V")
                    || message.equals("VIEW")) {
                    promptIndex = 1;
                    mode        = 'v';
                    continue;
                } 
                
                else if (   message.equals("M")
                    || message.equals("MOVE")) {
                    promptIndex = 1;
                    mode        = 'm';
                    continue;
                } else {
                    shortMoveMode();
                }

            }


            if (mode == 'm') {
                shortMoveMode();
            }            
            
            if ( mode == 'v' ) {
                doViewMode();
            }
            
        
        } while( gameDTO.winner().isEmpty() );


        drawBoard2();
    }

    private Pair<String,String> splitSrcDest( String splitMe ) throws IllegalArgumentException {
        

        Pattern p = Pattern.compile("([A-Z][0-9]+)([A-Z][0-9]+)");
        Matcher m = p.matcher( splitMe );

        if ( !m.find() ) {
            throw new IllegalArgumentException();
        }

        try {
            return new Pair<String,String>( m.group(1), m.group(2));
        } catch ( IndexOutOfBoundsException ioe) {
            throw new IllegalArgumentException();
        }
    }



    /**
     *      Prints the board to the TUI display
     * @param piece     Focus piece for which we are drawing moves and attacks
     */

    private void drawBoard2( String focusSquare ) {
        clearScreen();
        System.out.print("\n\n\n");
        System.out.println("                        HNEFATAFL\n");

        String currSquareCode = "";
        String pieceColour;
        String squareColour;

        Set<String> destMoves = new HashSet<String>();
        Set<String> attacks   = new HashSet<String>();


        for (MoveDTO moveDTO : gameDTO.moves() ) {
            if ( moveDTO.sourceLocation().equals( focusSquare )) {
                // Found correct source piece
                for ( DestResultDTO pair : moveDTO.destinationPairs() ) {
                    destMoves.add( pair.destLocation() );
                    attacks.addAll( pair.attackeeLocations() );
                }
            }
        }


        for (int i = Game.getNumRowsCols() - 1; i >= 0; --i) {
            System.out.print(ANSIColour.ANSI_CYAN);
            if (i == 9 || i == 10){
                System.out.print( "        " + String.valueOf(i + 1)  + "  ");     // Print row indices for '10' and '11'
            } else {
                System.out.print( "        " + String.valueOf(i + 1)  + "   ");    // Print single digit row indices
            }

            String row = String.valueOf(i + 1);

            
            for (int j = 0; j < Game.getNumRowsCols(); j++) {
                String col = ((char)(j + 'A')) + "";
                currSquareCode = col + row;

                if ( gameDTO.defenders().contains(currSquareCode) || gameDTO.kingSquare().equals(currSquareCode)) {
                    pieceColour = ANSIColour.ANSI_YELLOW;
                } else if ( gameDTO.attackers().contains( currSquareCode ) ) {
                    pieceColour = ANSIColour.ANSI_WHITE;
                } else {
                    pieceColour = "";
                }


                /* Set Square Background Colour */
                if ( destMoves.contains( currSquareCode ) ) {
                    squareColour = ANSIColour.ANSI_GREEN_BACKGROUND;
                } else if (attacks.contains( currSquareCode )) {
                    squareColour = ANSIColour.ANSI_RED_BACKGROUND;
                } else {
                    squareColour = "";
                }

                 /* Set piece colour */

                 if ( gameDTO.defenders().contains(currSquareCode) || gameDTO.kingSquare().equals(currSquareCode)) {
                    pieceColour = ANSIColour.ANSI_YELLOW;
                } else if ( gameDTO.attackers().contains( currSquareCode ) ) {
                    pieceColour = ANSIColour.ANSI_WHITE;
                } else {
                    pieceColour = "";
                }

                char lb, rb;
                if ( (i == 0 && j == 0) || (i == 0 && j == 10) || (i == 10 && j == 0) || (i == 10 && j == 10) || (i == 5 && j == 5) ) {
                    lb = '{';
                    rb = '}';
                } else {
                    lb = '[';
                    rb = ']';
                }

                /* Set piece string */
                String currPiece;
                
                if ( gameDTO.defenders().contains( currSquareCode ) || gameDTO.attackers().contains( currSquareCode)  ) {
                    currPiece = getPawnString();
                
                } else if ( gameDTO.kingSquare().equals( currSquareCode )) {
                    currPiece = getKingString();
                } else {
                    currPiece = " ";
                }

                String result = ANSIColour.ANSI_CYAN + lb + squareColour + pieceColour + currPiece + ANSIColour.ANSI_RESET + ANSIColour.ANSI_CYAN + rb;

                System.out.print( result );
            }
            System.out.println(infoMessage(i));

        }
        
        System.out.println("\n             A  B  C  D  E  F  G  H  I  J  K\n");
        System.out.print(infoMessage(-1));
    }




    boolean isOccupied(){
    
        return ( !gameDTO.attackers().contains(sourceLocation) ) 
                && ( !gameDTO.defenders().contains(sourceLocation) ) 
                && ( !gameDTO.kingSquare().equals(sourceLocation) );

    }





        private String infoMessage(int row) {
        switch (row) {
            
            case 10 :
                return "    Attacking       " + getColour("ATTACKING") + gameDTO.attackingPlayerName() + ANSIColour.ANSI_CYAN;
            case 9 :
                return "                    " + getColour("ATTACKING") + gameDTO.attackers().size() + ANSIColour.ANSI_CYAN;
            case 6 :
                return "    Defending       " + getColour("DEFENDING") +  gameDTO.defenderPlayerName() + ANSIColour.ANSI_CYAN;
            
            case 5 :
                return "                    " + getColour("DEFENDING") +  gameDTO.defenders().size() + ANSIColour.ANSI_CYAN;

            case 3 :
                return "    Turn            " + getColour(gameDTO.turn()) +  currentTurnPlayerName() + ANSIColour.ANSI_WHITE;
            case 2 :
                return "    Move no.        "   +  getColour(gameDTO.turn()) + gameDTO.turnCount() + ANSIColour.ANSI_CYAN;
            case 1 :
                int kingSurroundedSides = gameDTO.kingSurroundedSides();
                String sidesMessge = "    King Surrounded:  " + ANSIColour.ANSI_YELLOW + gameDTO.kingSurroundedSides() + ANSIColour.ANSI_CYAN;
                return kingSurroundedSides > 0 ? sidesMessge : "";
            case 0 :
                String winnerMsg = "";
                if ( !(gameDTO.winner().isEmpty()) ) {
                    String winnerSide = gameDTO.turn();
                    winnerMsg = "    Winner!         " + getColour( winnerSide ) + gameDTO.winner( ) + ANSIColour.ANSI_CYAN;
                }
                return winnerMsg;
            case -1 :
                return promptMessages.get(promptIndex);
            // case -2 :
            //     return promptMessages.get(11) + game.getWinner().getName();
            default:
                return "";
        }
    }

    private String currentTurnPlayerName(){
        return gameDTO.turn() == "ATTACKING" ? gameDTO.attackingPlayerName() : gameDTO.defenderPlayerName();
    }

    private String getColour( String side ) {
        return side == "ATTACKING" ? ANSIColour.ANSI_YELLOW : ANSIColour.ANSI_WHITE;
    }



    /**
     *      Prints the board to the TUI display
     * @param piece     Focus piece for which we are drawing moves and attacks
     */

    public void drawBoard2() {
        clearScreen();
        System.out.print("\n\n\n");
        System.out.println("                        HNEFATAFL\n");


        String currSquareCode = "";
        String pieceColour;

   
        // Draws empty board with no pieces
        for (int i = Game.getNumRowsCols() - 1; i >= 0; --i) { // rows
            System.out.print(ANSIColour.ANSI_CYAN);
            if (i == 9 || i == 10){
                System.out.print( "        " + String.valueOf(i + 1)  + "  ");     // Print row indices for '10' and '11'
            } else {
                System.out.print( "        " + String.valueOf(i + 1)  + "   ");    // Print single digit row indices
            }
            
            String row = String.valueOf(i + 1);


            for (int j = 0; j < Game.getNumRowsCols(); j++) {

                String col =  ((char) (j + 'A')) + "";
                

                currSquareCode = col + row;

                /* Set piece colour */

                if ( gameDTO.defenders().contains(currSquareCode) || gameDTO.kingSquare().equals(currSquareCode)) {
                    pieceColour = ANSIColour.ANSI_WHITE;
                } else if ( gameDTO.attackers().contains( currSquareCode ) ) {
                    pieceColour = ANSIColour.ANSI_YELLOW;
                } else {
                    pieceColour = "";
                }


                char lb, rb;
                if ( (i == 0 && j == 0) || (i == 0 && j == 10) || (i == 10 && j == 0) || (i == 10 && j == 10) || (i == 5 && j == 5) ) {
                    lb = '{';
                    rb = '}';
                } else {
                    lb = '[';
                    rb = ']';
                }

                /* Set piece string */
                String currPiece;
                
                if ( gameDTO.defenders().contains( currSquareCode ) || gameDTO.attackers().contains( currSquareCode)  ) {
                    currPiece = getPawnString();
                
                } else if ( gameDTO.kingSquare().equals( currSquareCode )) {
                    currPiece = getKingString();
                } else {
                    currPiece = " ";
                }

                String result = ANSIColour.ANSI_CYAN  + lb + pieceColour + currPiece + ANSIColour.ANSI_RESET + ANSIColour.ANSI_CYAN + rb;

                System.out.print( result );
            }
            System.out.println(infoMessage(i));
            // System.out.println();
        }
        
        System.out.println("\n             A  B  C  D  E  F  G  H  I  J  K\n");
        System.out.print(infoMessage(-1));

        

    }

    

    private static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
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

    public static boolean badSquareCode( String squareCode ) {
        return !squareCodeOK(squareCode);
    }


}
