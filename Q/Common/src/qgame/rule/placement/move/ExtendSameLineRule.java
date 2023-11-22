package qgame.rule.placement.move;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import qgame.state.map.Posn;
import qgame.state.Placement;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.board.EmptyBoardRule;
import qgame.state.IPlayerGameState;
import qgame.util.PosnUtil;

import static qgame.util.ValidationUtil.validateArg;

/**
 * Rule that checks if all placements extend the same row or column and
 * that there are no duplicate placements.
 */
public class ExtendSameLineRule extends MoveRule {

  private boolean allSame(List<Posn> posns, BiPredicate<Posn, Posn> func) {
    Posn first = posns.get(0);
    return posns
      .stream()
      .allMatch(otherElement -> func.test(first, otherElement));
  }

  /**
   * Returns true if all of the given placements are in the same row or column,
   * and if there are no duplicate placements in the list.
   * @param placements
   * @param state Not used in this implementation
   * @return
   */
  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState state) {
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

  @Override
  public boolean canAddPlacementToMove(Placement placement, List<Placement> placements) {

    List<Placement> testPlacements = new ArrayList<>(placements);
    testPlacements.add(placement);

    List<Posn> posns = testPlacements
    .stream()
    .map(Placement::posn)
    .toList();

    return allSame(posns, PosnUtil::sameCol) || allSame(posns, PosnUtil::sameRow);
  }

  @Override
  public BoardRule getBoardRule() {
    return new EmptyBoardRule();
  }

  @Override
  public MoveRule getMoveRule() {
    return this;
  }
}