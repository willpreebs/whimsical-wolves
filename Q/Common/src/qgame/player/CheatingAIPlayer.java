package qgame.player;

import qgame.action.TurnAction;
import qgame.player.strategy.CheatStrategy;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class CheatingAIPlayer implements Player {

    public enum Cheat {NONE, NOT_ADJACENT, NOT_OWNED,
        NOT_INLINE, NOT_ENOUGH_TILES, NOT_LEGAL_NEIGHBOR};

    private SimpleAIPlayer player;
    private Cheat cheat;
    private TurnStrategy backupStrategy;


    public CheatingAIPlayer(String name, TurnStrategy s, Cheat cheat) {
        this.cheat = cheat;
        TurnStrategy cheatingStrat = new CheatStrategy(cheat, s);
        this.backupStrategy = s;
        player = new SimpleAIPlayer(name, cheatingStrat);
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
    public void setup(IPlayerGameState state) throws IllegalStateException {
        player.setup(state);
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        player.newTiles(tiles);
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
        player.win(w);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CheatingAIPlayer) {
            return this.name().equals(((CheatingAIPlayer) o).name());
        }
        else {
            return false;
        }
    }


    public CheatingAIPlayer.Cheat getCheat() {
        return this.cheat;
    }
    public TurnStrategy strategy() {
        return this.backupStrategy;
    }


}
