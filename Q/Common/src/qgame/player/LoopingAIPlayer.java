package qgame.player;



import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.TurnStrategy;

/**
 * Represents a type of bad player that will pursue its
 * strategy and act normally until a specified method is
 * called countLimit times, after which it will get stuck
 * in an infinite loop.
 */
public class LoopingAIPlayer extends SimpleAIPlayer {

    private FailStep step;
    private int countLimit;

    private int count = 0;

    public LoopingAIPlayer(String name, TurnStrategy strat, FailStep step, int countLimit) {
        super(name, strat);
        this.step = step;
        this.countLimit = countLimit;
    }

    @Override
    public String name() {
        return super.name();
    }

    private void loopIfLimitReached(FailStep step) {

        if (this.step.equals(step)) {
            count++;
        }
        while (count == countLimit) {
            //infinite loop
        }
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState state) throws IllegalStateException {

        loopIfLimitReached(FailStep.TAKE_TURN);
        return super.takeTurn(state);
    }

    @Override
    public void setup(IPlayerGameState state, Bag<Tile> tiles) throws IllegalStateException {
        loopIfLimitReached(FailStep.SETUP);
        super.setup(state, tiles);
    }

    @Override
    public void newTiles(Bag<Tile> tiles) throws IllegalStateException {
        loopIfLimitReached(FailStep.NEW_TILES);
        super.newTiles(tiles);
    }

    @Override
    public void win(boolean w) throws IllegalStateException {
        loopIfLimitReached(FailStep.WIN);
        super.win(w);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    public FailStep failStep() {
        return this.step;
    }
    public int getCountLimit(){return this.countLimit;}
}
