package qgame.rule.scoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.state.Placement;

/**
 * Represents a scoring rule that gives points if the given placements form a Q. That is, when there
 * is a sequence of 6 contiguous tiles that either has all 6 shapes or all 6 colors. 6 Points are
 * awarded for completing a Q.
 */
public class QRule extends CrawlingRule {

  private final int Q_BONUS;

  public QRule(int bonus) {
    this.Q_BONUS = bonus;
  }

  // Returns a stream of the tiles at the given positions in the qGameState
  private Stream<Tile> tilesFrom(List<Posn> positions, IMap map) {
    return positions.stream().map(map::getTileAtPosn);
  }

  // Checks if a sequence of positions is a Q in a given PlayerGameState
  private int qPoints(List<Posn> positions, IMap map) {
    if (positions.size() != Tile.Color.values().length
      || positions.size() != Tile.Shape.values().length) {
      return 0;
    }
    Set<Tile.Color> colorSet = new HashSet<>(List.of(Tile.Color.values()));
    Set<Tile.Shape> shapeSet = new HashSet<>(List.of(Tile.Shape.values()));


    Set<Tile.Color> colorsSeen =
      tilesFrom(positions, map).map(Tile::color).collect(Collectors.toSet());

    Set<Tile.Shape> shapesSeen =
      tilesFrom(positions, map).map(Tile::shape).collect(Collectors.toSet());

    if (colorSet.equals(colorsSeen) || shapeSet.equals(shapesSeen)) {
      return Q_BONUS;
    }
    return 0;
  }

  // Find all horizontal and vertical contiguous sequences connected to a given position.

  // POSSIBLE BUG HERE SHOULD EXPLORE MULTI PLACEMENT IN A LINE WITH A Q
  private Set<Set<Posn>> findVertAndHorizontalSequences(Posn posn, IMap map) {
    Set<Posn> verticals = new HashSet<>();
    exploreVertical(posn, map, verticals);
    verticals.add(posn);

    Set<Posn> horizontals = new HashSet<>();
    exploreHorizontal(posn, map, horizontals);
    horizontals.add(posn);

    return new HashSet<>(List.of(verticals, horizontals));

  }

  // Calculates points earned for if there were any Qs completed in the game state by the given
  // placement. Returns 0 if no Qs satisfied by a placement.
  private void collectStreaks(Placement placement, IMap map, Set<Set<Posn>> streaks) {
    Posn posn = placement.posn();
    Set<Set<Posn>> possibleStreaks = findVertAndHorizontalSequences(posn, map);
    streaks.addAll(possibleStreaks);
  }

  @Override
  public int pointsFor(List<Placement> placements, IMap map) {
    Set<Set<Posn>> streaks = new HashSet<>();

    placements
      .forEach(placement -> collectStreaks(placement, map, streaks));
    return streaks
      .stream()
      .map(ArrayList::new)
      .mapToInt(list -> qPoints(list, map))
      .sum();
  }
}
