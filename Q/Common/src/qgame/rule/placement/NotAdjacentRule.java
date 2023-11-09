package qgame.rule.placement;

import qgame.state.Placement;
import qgame.state.map.IMap;
import qgame.state.map.Posn;
import static qgame.util.PosnUtil.neighbors;

/**
 * Cheating Rule: rule satisfied if the placement
 * location lacks a neighbor.
 */
public class NotAdjacentRule extends BoardRule {
    

  private boolean hasNeighbor(Posn posn, IMap map) {
    return neighbors(posn)
      .stream()
      .anyMatch(map::posnHasTile);
  }
  protected boolean legalPlacement(Placement move, IMap map) {
    Posn posn = move.posn();
    return !map.posnHasTile(posn) && !hasNeighbor(posn, map);
  }

}
