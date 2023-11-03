package qgame.rule.placement;

import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.Placement;

import static qgame.util.PosnUtil.neighbors;

/**
 * Represents a rule that checks that each placement extends the board according to the Q game
 * board extending rules.
 */
public class ExtendsBoardRule extends BoardRule {

  private boolean hasNeighbor(Posn posn, QGameMap map) {
    return neighbors(posn)
      .stream()
      .anyMatch(map::posnHasTile);
  }
  protected boolean legalPlacement(Placement move, QGameMap map) {
    Posn posn = move.posn();
    return !map.posnHasTile(posn) && hasNeighbor(posn, map);
  }
}
