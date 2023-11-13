package qgame.referee;


import qgame.action.TurnAction;
import qgame.state.Bag;
import qgame.state.map.Tile;
import qgame.player.Player;
import qgame.state.IPlayerGameState;

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
  public TurnAction takeTurn(IPlayerGameState ref) throws IllegalStateException {
    throw new IllegalStateException("Disconnected.");
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
      if (o instanceof DisconnectPlayer) {
          return this.name().equals(((DisconnectPlayer) o).name());
      }
      else {
          return false;
      }
  }
}
