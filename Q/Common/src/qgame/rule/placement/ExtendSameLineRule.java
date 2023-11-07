package qgame.rule.placement;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import qgame.state.map.Posn;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.util.PosnUtil;

import static qgame.util.ValidationUtil.validateArg;

/**
 * Represents a placement rule that checks if all placements extend the same row or column.
 */
public class ExtendSameLineRule extends ARule {

  private boolean allSame(List<Posn> posns, BiPredicate<Posn, Posn> func) {
    Posn first = posns.get(0);
    return posns
      .stream()
      .allMatch(otherElement -> func.test(first, otherElement));
  }

  @Override
  public boolean validPlacements(List<Placement> placements, IPlayerGameState map) {
    validateArg(Predicate.not(List::isEmpty), placements, "Placements cannot be empty.");
    List<Posn> posns = placements
      .stream()
      .map(Placement::posn)
      .toList();
    List<Posn> uniquePosns = posns
      .stream()
      .distinct()
      .toList();
    return (allSame(posns, PosnUtil::sameCol) || allSame(posns, PosnUtil::sameRow))
      && posns.size() == uniquePosns.size();
  }
}
