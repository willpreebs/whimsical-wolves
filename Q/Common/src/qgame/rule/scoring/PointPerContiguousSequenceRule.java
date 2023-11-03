package qgame.rule.scoring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import qgame.state.map.Posn;
import qgame.state.map.QGameMap;
import qgame.state.Placement;
import qgame.util.PosnUtil;


/**
 * Represents a scoring rule that awards 1 point for every tile in a contiguous sequence of
 * tiles that is extended by the given placements.
 */
public class PointPerContiguousSequenceRule extends CrawlingRule {


  private void updateSetsWithConnectedSequences(Placement placement, QGameMap map,
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
                                                 QGameMap map) {
    Set<Posn> horizontal = new HashSet<>();
    Set<Posn> vertical = new HashSet<>();
    placements.forEach(
      placement -> updateSetsWithConnectedSequences(placement, map, horizontal, vertical));
    return List.of(horizontal, vertical);
  }

  @Override
  public int pointsFor(List<Placement> placements, QGameMap map) {
    List<Set<Posn>> connected = findAllContiguousSequences(placements, map);
    return connected
      .stream()
      .mapToInt(Set::size)
      .sum();
  }
}
