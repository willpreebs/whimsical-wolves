package qgame.rule.placement;

import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.map.Tile;
import qgame.state.Placement;
import qgame.util.TileUtil;

import static qgame.util.PosnUtil.neighbors;
import static qgame.util.PosnUtil.sameCol;
import static qgame.util.PosnUtil.sameRow;

/**
 * An implementation of a placement rule that checks
 */
public class MatchTraitRule extends BoardRule {

  private boolean sameTraitSubset(Tile tile, List<Tile> lineOfNeighbors) {
    return lineOfNeighbors.stream().allMatch(neighbor -> TileUtil.sameShape(tile, neighbor))
      || lineOfNeighbors.stream().allMatch(neighbor -> TileUtil.sameColor(tile, neighbor));
  }
  private boolean sameTraitAcrossTiles(Tile tile, List<Tile> verticalNeighbors,
                                       List<Tile> horizontalNeighbors) {
    return sameTraitSubset(tile, verticalNeighbors) && sameTraitSubset(tile, horizontalNeighbors);
  }
  protected boolean legalPlacement(Placement move, QGameMap qGameBoard) {
    Posn posn = move.posn();
    Tile tile = move.tile();
    if (qGameBoard.posnHasTile(posn)) {
      return false;
    }
    List<Posn> existingNeighbors = neighbors(posn).stream().filter(qGameBoard::posnHasTile).toList();

    List<Tile> vertNeighbors = existingNeighbors
      .stream()
      .filter(neighbor -> sameCol(posn, neighbor))
      .map(qGameBoard::getTileAtPosn).toList();

    List<Tile> horizNeighbors = existingNeighbors
      .stream()
      .filter(neighbor -> sameRow(posn, neighbor))
      .map(qGameBoard::getTileAtPosn).toList();

    return sameTraitAcrossTiles(tile, vertNeighbors, horizNeighbors);
  }
}
