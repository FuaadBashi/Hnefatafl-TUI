package ws.aperture.hnefatafl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ws.aperture.hnefatafl.model.enums.Side;

public record GameDTO(  String attackingPlayerName, String defenderPlayerName,
                        String turn, int turnCount,
                        String kingSquare, List<String> defenders, List<String> attackers,
                        String winner, String winResult,
                        List<MoveDTO> moves, int kingSurroundedSides) {
    

    public static GameDTO generateDTO( Game game ) {
        String attackerName = game.getPlayerName(Side.ATTACKING);
        String defenderName = game.getPlayerName(Side.DEFENDING);
        Side turnSide       = game.getPlayerTurn();
        String turnString   = turnSide.toString();
        int turnCount       = game.getMoveCount();

        String kingSquare = game.getKingSquare().toString();

        List<String> attackerPieces = game.getAttackers()
                                      .stream()
                                      .map( p -> p.getSquare().toString())
                                      .collect( Collectors.toList() );

        List<String> defenderPieces = game.getDefenders()
                                     .stream()
                                     .map( p -> p.getSquare().toString())
                                     .collect( Collectors.toList() );

        String winner = game.getWinner();
        String winResult = "";

        List<MoveDTO> moves = new ArrayList<MoveDTO>();


        for ( Piece p : game.getPieces( turnSide ) ) {
            MoveDTO move = MoveDTO.generateDTO(p);
            moves.add(move);
        }
        


        return new GameDTO(
            attackerName, defenderName,
            turnString, turnCount,
            kingSquare, defenderPieces, attackerPieces,
            winner, winResult,
            moves, game.getKingSurroundedSides()
        );
    }
}
