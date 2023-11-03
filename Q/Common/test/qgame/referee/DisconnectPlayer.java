package qgame.referee;

import java.util.List;

import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.player.Player;
import qgame.state.PlayerGameState;

public class DisconnectPlayer implements Player {

  private String name;

  public DisconnectPlayer(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return "Billy Bob";
  }

  @Override
  public TurnAction takeTurn(PlayerGameState ref) throws IllegalStateException {
    throw new IllegalStateException("Disconnected.");
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
