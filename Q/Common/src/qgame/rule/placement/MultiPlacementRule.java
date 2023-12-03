package qgame.rule.placement;

import java.util.ArrayList;
import java.util.List;

import qgame.state.Placement;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.board.MultiBoardRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.move.MultiMoveRule;
import qgame.state.IPlayerGameState;

/**
 * An and predicate of ARule, is satisfied if all sub-rules
 * are also satisfied.
 */
public class MultiPlacementRule extends ARule {

  private final List<IPlacementRule> rules;

  public MultiPlacementRule(IPlacementRule... rules) {
    this.rules = new ArrayList<>(List.of(rules));
  }
  public MultiPlacementRule(List<IPlacementRule> rules) {
    this.rules = new ArrayList<>(rules);
  }

  public MultiPlacementRule getBoardRules() {
    List<IPlacementRule> boardRules = new ArrayList<>();

    for (IPlacementRule r : this.rules) {
      if (r instanceof BoardRule) {
        boardRules.add(r);
      }
    }

    return new MultiPlacementRule(boardRules);
  }

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState gameState) {

    for (IPlacementRule r : this.rules) {
      if (!r.isPlacementListLegal(placements, gameState)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Returns a MultiBoardRule containing all of the BoardRules
   * in this.rules
   */
  @Override
  public BoardRule getBoardRule() {
    List<BoardRule> bRules = new ArrayList<>();

    for (IPlacementRule r : this.rules) {
      if (r instanceof BoardRule) {
        bRules.add((BoardRule) r);
      }
    }

    return new MultiBoardRule(bRules);
  }

  /**
   * Returns a MultiMoveRule containing all of the MoveRules
   * in this.rules
   */
  @Override
  public MoveRule getMoveRule() {
    List<MoveRule> mRules = new ArrayList<>();

    for (IPlacementRule r : this.rules) {
      if (r instanceof MoveRule) {
        mRules.add((MoveRule) r);
      }
    }

    return new MultiMoveRule(mRules);
  }
}