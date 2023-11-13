package qgame.player;

import org.junit.internal.runners.statements.Fail;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.IPlayerGameState;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

import qgame.player.DummyAIPlayer.FailStep;
import qgame.player.strategy.TurnStrategy;

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
    public void setup(IPlayerGameState state) throws IllegalStateException {
        loopIfLimitReached(FailStep.SETUP);
        super.setup(state);
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
}
