package qgame.rule.placement.board;

import static qgame.util.PosnUtil.neighbors;

import java.util.List;
import java.util.function.BiPredicate;

import qgame.rule.placement.move.EmptyMoveRule;
import qgame.rule.placement.move.MoveRule;
import qgame.state.Placement;
import qgame.state.map.QMap;
import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.util.PosnUtil;
import qgame.util.TileUtil;

/**
 * Rule that checks whether a proposed placement tile matches in one trait (shape or color)
 * with its neighbors. The vertical neighbors must all match in the same trait, and the 
 * horizontal neighbors must all match in the same trait (can be different from the shared
 * vertical trait). 
 */
public class MatchTraitRule extends BoardRule {

  private boolean sameTraitSubset(Tile tile, List<Tile> lineOfNeighbors) {
    return lineOfNeighbors.stream().allMatch(neighbor -> TileUtil.sameShape(tile, neighbor))
      || lineOfNeighbors.stream().allMatch(neighbor -> TileUtil.sameColor(tile, neighbor));
  }
  
  @Override
  public boolean isLegalPlacementOnBoard(Placement move, QMap qGameBoard) {
    Posn posn = move.posn();
    Tile tile = move.tile();
    if (qGameBoard.posnHasTile(posn)) {
      return false;
    }
    List<Posn> tiledNeighbors = this.getAllNeighborsWithTile(posn, qGameBoard);

    List<Tile> vertNeighbors = this.filterTileLocationsByPredicate
            (tiledNeighbors,posn, PosnUtil::sameCol, qGameBoard);

    List<Tile> horizNeighbors = this.filterTileLocationsByPredicate
            (tiledNeighbors,posn, PosnUtil::sameRow, qGameBoard);

    return sameTraitSubset(tile, vertNeighbors)
            && sameTraitSubset(tile, horizNeighbors);
  }
  
  private List<Posn> getAllNeighborsWithTile(Posn posn, QMap board){
    return neighbors(posn)
            .stream()
            .filter(board::posnHasTile)
            .toList();
  }
  
  private List<Tile> filterTileLocationsByPredicate
          (List<Posn> posns, Posn posn, BiPredicate<Posn, Posn> func, QMap board){
    return posns
            .stream()
            .filter(neighbor -> func.test(posn, neighbor))
            .map(board::getTileAtPosn).toList();
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
