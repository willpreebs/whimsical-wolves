package qgame.player;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;

public class QPlayer implements Player {

    String name;

    public QPlayer(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException {
        return new PassAction();
    }

    @Override
    public void setup(IPlayerGameState state, Bag<Tile> tiles) {
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Player) {
            Player p = (Player) other;
            return this.name.equals(p.name());
        }
        else {
            return false;
        }
    }
    
}
