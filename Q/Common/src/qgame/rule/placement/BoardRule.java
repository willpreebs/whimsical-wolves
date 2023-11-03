package qgame.rule.placement;

import java.util.List;

import qgame.state.map.QGameMap;
import qgame.state.map.QGameMapImpl;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

abstract class BoardRule extends ARule {

  protected abstract boolean legalPlacement(Placement placement, QGameMap map);

  public boolean validPlacements(List<Placement> placements, PlayerGameState map) {
    QGameMap board = new QGameMapImpl(map.viewBoard().getBoardState());
    for (Placement placement : placements) {
      if (!legalPlacement(placement, board)) {
        return false;
      }
      board.placeTile(placement);
    }
    return true;
  }
}
