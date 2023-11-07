package qgame.rule.placement;

import java.util.List;

import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;

abstract class BoardRule extends ARule {

  protected abstract boolean legalPlacement(Placement placement, IMap map);

  public boolean validPlacements(List<Placement> placements, IPlayerGameState map) {
    IMap board = new QMap(map.viewBoard().getBoardState());
    for (Placement placement : placements) {
      if (!legalPlacement(placement, board)) {
        return false;
      }
      board.placeTile(placement);
    }
    return true;
  }
}
