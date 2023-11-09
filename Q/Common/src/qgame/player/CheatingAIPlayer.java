package qgame.player;

import qgame.action.TurnAction;
import qgame.player.strategy.CheatStrategy;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class CheatingAIPlayer implements Player {

    public enum Cheat {NONE, NOT_ADJACENT, NOT_OWNED, NOT_INLINE, NOT_ENOUGH_TILES, NOT_LEGAL_NEIGHBOR};

    private SimpleAIPlayer player;
    private Cheat cheat;


    public CheatingAIPlayer(String name, TurnStrategy s, Cheat cheat) {
        player = new SimpleAIPlayer(name, s);
        this.cheat = cheat;
    }

    @Override
    public String name() {
        return player.name();
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException {
        return player.takeTurn(state);
    }

    @Override
    public void setup(IMap map, Bag<Tile> tiles) throws IllegalStateException {
        player.setup(map, tiles);
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        player.newTiles(tiles);
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'win'");
    }


    public CheatingAIPlayer.Cheat getCheat() {
        return this.cheat;
    }
    public TurnStrategy strategy() {
        return this.player.strategy();
    }
}
