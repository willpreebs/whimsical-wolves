package qgame.referee;

import java.util.List;

import qgame.action.PlaceAction;
import qgame.action.TurnAction;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.state.map.TileImpl;
import qgame.player.strategy.TurnStrategy;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

public class BadTurnStrategy implements TurnStrategy {

  @Override
  public TurnAction chooseAction(PlayerGameState state) {
    Placement placement = new Placement(new Posn(1000, -1000),
      new TileImpl(Tile.Color.ORANGE, Tile.Shape.CIRCLE));
    return new PlaceAction(List.of(placement));
  }
}
