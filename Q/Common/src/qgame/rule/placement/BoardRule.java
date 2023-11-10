package qgame.rule.placement;

import java.awt.Image;
import java.util.List;

import qgame.state.map.IMap;
import qgame.state.map.QMap;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;

/**
 * Abstract class that extends ARule, for placement rules that specifically
 * affect the Board, for every legal placement, place the tile. If a placement
 * violates a rule, stop placing tiles and return false.
 */
abstract class BoardRule extends ARule {

  protected abstract boolean legalPlacement(Placement placement, IMap map);

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState state) {
    IMap board = new QMap(state.getBoard().getBoardState());
    for (Placement placement : placements) {
      if (!legalPlacement(placement, board)) {
        return false;
      }
      board.placeTile(placement);
    }
    return true;
  }
}
