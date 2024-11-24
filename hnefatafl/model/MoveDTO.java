package ws.aperture.hnefatafl.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public record MoveDTO(String sourceLocation, List<DestResultDTO> destinationPairs, List<String> moveLogs ) {

    public static MoveDTO generateDTO( Piece piece) {

        String sourceLocation = piece.getSquare().toString();
        
        List<DestResultDTO> destinationPairs = new ArrayList<DestResultDTO>();

        for ( Square s : piece.getMoves()) {
            
            String destLoc = s.toString();
            
            List<String> attackLocs = piece.getAttacks(s)
                                      .stream()
                                      .map( a -> a.toString() )
                                      .collect( Collectors.toList() );
            DestResultDTO DestResultDTO = new DestResultDTO(destLoc, attackLocs);
           
            destinationPairs.add( DestResultDTO );
            
        }
        
        List<String> moveLog = new ArrayList<String>();
        
        return new MoveDTO(sourceLocation, destinationPairs, moveLog);
    }

}