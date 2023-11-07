package qgame.rule.scoring;

import java.util.Set;

import qgame.state.map.Posn;
import qgame.state.map.IMap;

/**
 * Represents a rule that needs to find all contiguous tiles in a given direction.
 */
public abstract class CrawlingRule implements ScoringRule {

  /**
   * From the start position, keep iterating positions using the given deltas, updating the set
   * of connected positions, until a position without a tile is found.
   * @param starting The starting position for the iteration.
   * @param map The game board to search through.
   * @param posnsConnected A set representing all the positions found in the search. Not meant to
   *                      be read from. Only appended to.
   * @param deltaY The change in y value for the position.
   * @param deltaX The change in x value for the position.
   */
  private void exploreInDirection(Posn starting, IMap map, Set<Posn> posnsConnected,
                                    int deltaY, int deltaX) {
    starting = new Posn(starting.y() + deltaY, starting.x() + deltaX);
    while (map.posnHasTile(starting)) {
      posnsConnected.add(starting);
      starting = new Posn(starting.y() + deltaY, starting.x() + deltaX);
    }
  }

  // Find all tiles in a contiguous sequence with a given position vertically
  protected void exploreVertical(Posn starting, IMap map, Set<Posn> posnsConnected) {
    exploreInDirection(starting, map, posnsConnected, -1, 0);
    exploreInDirection(starting, map, posnsConnected, 1, 0);
  }

  // Find all tiles in a contiguous sequence with a given position horizontally
  protected void exploreHorizontal(Posn starting, IMap map, Set<Posn> posnsConnected) {
    exploreInDirection(starting, map, posnsConnected, 0, -1);
    exploreInDirection(starting, map, posnsConnected, 0, 1);
  }
}
