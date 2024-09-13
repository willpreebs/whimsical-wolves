package qgame.rule.scoring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import qgame.state.map.Posn;
import qgame.state.map.QMap;
import qgame.state.IGameState;
import qgame.state.Placement;
import qgame.util.PosnUtil;


/**
 * Represents a scoring rule that awards 1 point for every tile in a contiguous sequence of
 * tiles that is extended by the given placements.
 */
public class PointPerContiguousSequenceRule extends CrawlingRule {

  private final int POINTS_PER_CONTIGUOUS_TILE;

  // private final int DEFAULT = 1;

  // public PointPerContiguousSequenceRule() {
  //   POINTS_PER_CONTIGUOUS_TILE = DEFAULT;
  // }

  public PointPerContiguousSequenceRule(int pointsPerContiguousTile) {
    POINTS_PER_CONTIGUOUS_TILE = pointsPerContiguousTile;
  }

  private void updateSetsWithConnectedSequences(Placement placement, QMap map,
                                          Set<Posn> horizontalTiles, Set<Posn> verticalTiles) {
    Posn posn = placement.posn();
    if (PosnUtil.hasVerticalNeighbor(posn,map)) {
      verticalTiles.add(posn);
      exploreVertical(posn, map, verticalTiles);
    }
    if (PosnUtil.hasHorizontalNeighbor(posn, map)) {
      horizontalTiles.add(posn);
      exploreHorizontal(posn, map, horizontalTiles);
    }

  }

  private List<Set<Posn>> findAllContiguousSequences(List<Placement> placements,
                                                 QMap map) {
    Set<Posn> horizontal = new HashSet<>();
    Set<Posn> vertical = new HashSet<>();
    placements.forEach(
      placement -> updateSetsWithConnectedSequences(placement, map, horizontal, vertical));
    return List.of(horizontal, vertical);
  }

  @Override
  public int pointsFor(List<Placement> placements, IGameState state) {
    QMap map = state.getBoard();
    List<Set<Posn>> connected = findAllContiguousSequences(placements, map);
    return connected
      .stream()
      .mapToInt(Set::size)
      .sum() * POINTS_PER_CONTIGUOUS_TILE;
  }
}
