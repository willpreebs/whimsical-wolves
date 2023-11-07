package qgame.observer;

import static qgame.util.ValidationUtil.nonNullObj;

import java.util.Collection;
import java.util.List;

import qgame.player.PlayerInfo;
import qgame.state.Bag;
import qgame.state.IGameState;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Tile;

public class QGameObserver implements IGameObserver {

    // List<IGameState> previous;
    // IGameState current;
    // IGameState next;

    // GameStates in order that they occur in the game
    List<IGameState> states;
    int stateIndex = 0;


    @Override
    public void receiveState(IGameState state) {
        nonNullObj(state, "State cannot be null");
        states.add(state);
        // render gui?
    }

    public void next() {
        stateIndex++;
    }

    public void previous() {
        stateIndex--;
    }


    @Override
    public void gameOver() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gameOver'");
    }

    public void saveGuiAsPng() {

    }


    
}
