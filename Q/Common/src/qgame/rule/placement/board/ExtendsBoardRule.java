package qgame.rule.placement.board;

import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.Placement;

import static qgame.util.PosnUtil.neighbors;

import qgame.rule.placement.move.EmptyMoveRule;
import qgame.rule.placement.move.MoveRule;

/**
 * Represents a rule that checks that each placement extends the board according to the Q game
 * board extending rules.
 */
public class ExtendsBoardRule extends BoardRule {

  private boolean hasNeighbor(Posn posn, IMap map) {
    return neighbors(posn)
      .stream()
      .anyMatch(map::posnHasTile);
  }

  @Override
  public boolean isLegalPlacementOnBoard(Placement move, IMap map) {
    Posn posn = move.posn();
    return !map.posnHasTile(posn) && hasNeighbor(posn, map);
  }

  @Override
  public BoardRule getBoardRule() {
    return this;
  }

  @Override
  public MoveRule getMoveRule() {
    return new EmptyMoveRule();
  }
}
