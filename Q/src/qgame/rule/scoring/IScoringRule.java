package qgame.rule.scoring;

import java.util.List;

import qgame.state.IGameState;
import qgame.state.Placement;

/**
 * Represents a rule for assigning points for placements in a Q game.
 * Currently, all scoring rules should have natural numbers as their outputs.
 */
public interface IScoringRule {
  /**
   * Returns the points earned by the list of placements on the game state according to the rule.
   * @param placements The list of placements to score. This class operates assuming that each
   *                   placement in placements is in sequential order, and that they extend
   *                   a the given game state's board properly.
   * @param map The game map to score
   * @return The points earned by the given placements for this rule.
   */
  int pointsFor(List<Placement> placements, IGameState state);
}
