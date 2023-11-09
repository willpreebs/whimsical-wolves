package qgame.rule.placement;

import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;

public abstract class ARule implements PlacementRule {

  @Override
  public List<Posn> validPositionsForTile(Tile t, IPlayerGameState gameState) {
    List<Posn> validPosns = gameState.getBoard().validPositions();
    return new ArrayList<>(
      validPosns
      .stream()
      .map(posn -> new Placement(posn, t))
      .filter(placement -> this.isPlacementListLegal(List.of(placement), gameState))
      .map(Placement::posn)
      .toList());
  }
}
