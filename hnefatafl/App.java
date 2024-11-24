package ws.aperture.hnefatafl;
// import ws.aperture.hnefatafl.TUI;

import ws.aperture.hnefatafl.model.Game;
import ws.aperture.hnefatafl.model.GameDTO;

public class App {

    public static void main( String[] args ) {

     
        TUI tui = new TUI();

        Args gameArgs = tui.startScreen();

        Game game = new Game(gameArgs.attackerName(), gameArgs.defenderName(), gameArgs.debugMode());

        GameDTO gameDTO = GameDTO.generateDTO(game);

        tui.start(gameDTO, game);
    
    }
}
