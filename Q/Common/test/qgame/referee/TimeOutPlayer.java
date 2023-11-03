package qgame.referee;

import java.util.List;

import qgame.action.PassAction;
import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.player.Player;
import qgame.state.PlayerGameState;

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
  public TurnAction takeTurn(PlayerGameState ref) throws IllegalStateException {
    try {
      Thread.sleep(this.timeToWait);
    } catch (InterruptedException e) {
      throw new IllegalStateException("Time out interrupted.");
    }
    return new PassAction();
  }

  @Override
  public void setup(QGameMap map, Bag<Tile> tiles) throws IllegalStateException {

  }

  @Override
  public void newTiles(Bag<Tile> tiles) throws IllegalStateException {

  }

  @Override
  public void win(boolean w) throws IllegalStateException {

  }
}
