package qgame.referee;

import java.util.List;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.player.Player;
import qgame.state.IPlayerGameState;

class TimeOutPlayer implements Player {

  private final int timeToWait;
  private final String name;

  public TimeOutPlayer(String name, int timeToWait) {
    this.timeToWait = timeToWait;
    this.name = name;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public TurnAction takeTurn(IPlayerGameState ref) throws IllegalStateException {
    try {
      Thread.sleep(this.timeToWait);
    } catch (InterruptedException e) {
      throw new IllegalStateException("Time out interrupted.");
    }
    return new PassAction();
  }

  @Override
  public void setup(IPlayerGameState state) throws IllegalStateException {

  }

  @Override
  public void newTiles(Bag<Tile> tiles) throws IllegalStateException {

  }

  @Override
  public void win(boolean w) throws IllegalStateException {

  }

  @Override
  public boolean equals(Object o) {
      if (o instanceof TimeOutPlayer) {
          return this.name().equals(((TimeOutPlayer) o).name());
      }
      else {
          return false;
      }
  }
}
