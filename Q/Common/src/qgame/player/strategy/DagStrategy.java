package qgame.player.strategy;

import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.state.PlayerGameState;
import qgame.util.PosnUtil;

/**
 * Represents a strategy where the player looks for the smallest tile
 * that can extend the current board. If there are multiple tiles of
 * equally small value (as determined in the Tile Util comparator), it
 * picks the one with the smallest row-column order.
 */
public class DagStrategy extends SmallestRowColumnTileStrategy {
  public DagStrategy(PlacementRule ruleBook) {
    super(ruleBook);
  }

  protected Placement makePlacementGivenPositions(PlayerGameState currentState,
                                    List<Posn> legalPlaces) {
    Tile bestTile = bestTile(currentState);
    legalPlaces.sort(PosnUtil::rowColumnCompare);
    Posn posn = legalPlaces. get(0);
    return new Placement(posn, bestTile);
  }
}
