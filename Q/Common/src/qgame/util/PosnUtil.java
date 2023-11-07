package qgame.util;

import java.util.List;
import java.util.function.BiPredicate;

import qgame.state.map.Posn;
import qgame.state.map.IMap;

/**
 * Utilities class which abstracts methods used across several classes
 * on Posn objects.
 */
public class PosnUtil {
  /**
   * Comparator posns by  row-column order.
   * Compares the rows and returns a negative number if the first posn has a
   * lower y value than the second posn and positive if the second posn has a lower y value. If
   * the y values are the same, return the same comparison on the x values.
   * @param posn1 the first posn being compared
   * @param posn2 the second posn being compared
   * @return the comparison result ie an int
   */
  public static int rowColumnCompare(Posn posn1, Posn posn2) {
    if (posn1.y() == posn2.y()) {
      return posn1.x() - posn2.x();
    }
    return posn1.y() - posn2.y();
  }

  /**
   * Checks that two positions are in the same row on the board.
   * @param posn1 the first position
   * @param posn2 the second position
   * @return a boolean determining if the posns are in the same row
   */
  public static boolean sameRow(Posn posn1, Posn posn2) {
    return posn1.y() == posn2.y();
  }

  /**
   * Checks that two positions are in the same column on the board.
   * @param posn1 the first position
   * @param posn2 the second position
   * @return a boolean determining if the posns are in the same column
   */
  public static boolean sameCol(Posn posn1, Posn posn2) {
    return posn1.x() == posn2.x();
  }

  /**
   * Gets all the positions of the neighbor tiles of a given position.
   * @param posn the position which is used to find any neighbors
   * @return a list of the positions of all neighbor tiles
   * @throws IllegalArgumentException if a null posn is passed in
   */
  public static List<Posn> neighbors(Posn posn) throws IllegalArgumentException{
    ValidationUtil.nonNullObj(posn, "Posn cannot be null.");
    return List.of(new Posn(posn.y(), posn.x() + 1), new Posn(posn.y(), posn.x() - 1),
      new Posn(posn.y() - 1, posn.x()), new Posn(posn.y() + 1, posn.x()));
  }

  public static boolean hasVerticalNeighbor(Posn posn, IMap state) {
    return hasDirectionalNeighbor(posn, state, PosnUtil::sameCol);
  }

  public static boolean hasHorizontalNeighbor(Posn posn, IMap state) {
    return hasDirectionalNeighbor(posn, state, PosnUtil::sameRow);
  }

  public static boolean hasDirectionalNeighbor(Posn posn, IMap state, BiPredicate<Posn,
    Posn> pred) {
    List<Posn> neighbors = neighbors(posn);

    return !neighbors
      .stream()
      .filter(neighbor -> pred.test(posn, neighbor))
      .filter(state::posnHasTile)
      .toList()
      .isEmpty();
  }
}
