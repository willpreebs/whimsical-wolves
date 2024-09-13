package qgame.rule.placement.board;

import java.util.ArrayList;
import java.util.List;

import qgame.rule.placement.ARule;
import qgame.state.IPlayerGameState;
import qgame.state.Placement;
import qgame.state.map.QMap;
import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.map.Tile;

/**
 * Abstract class that extends ARule, for placement rules that specifically
 * affect the Board, for every legal placement, place the tile. If a placement
 * violates a rule, stop placing tiles and return false.
 */
public abstract class BoardRule extends ARule {

  public abstract boolean isLegalPlacementOnBoard(Placement placement, QMap map);

  public List<Placement> getValidPlacements(Tile tile, QMap map) {
      List<Posn> validPosns = map.validPositions();

      List<Placement> validPlacements = new ArrayList<>();

      validPosns.forEach(posn -> validPlacements.add(new Placement(posn, tile)));

      return validPlacements.stream()
        .filter(p -> isLegalPlacementOnBoard(p, map)).toList();
  } 

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState state) {
    QMap board = new QMap(state.getBoard().getBoardState());
    for (Placement placement : placements) {
      if (!isLegalPlacementOnBoard(placement, board)) {
        return false;
      }
      board.placeTile(placement);
    }
    return true;
  }
}
