package qgame.rule.scoring;

import java.util.List;

import qgame.state.Placement;
import qgame.state.PlayerGameState;
import qgame.state.QGameState;

/**
 * Represents a collection of scoring rules for the QGame.
 */
public interface ScoringRuleBook {
  /**
   * Adds the points for each of the scoring rules contained in this rule book.
   * @param placements The list of placements to score. There is an assumption that the placements
   *                   follow the basic board extension rules on the given game state, and are in
   *                   sequential order, i.e.
   *                   (placements = [first placement, second placement, ...])
   * @param state The Q game state for these placements to be scored on.
   * @return The total of the points earned from the placements in the given game state according
   * to the rules in the rulebook.
   */
  int pointsFor(List<Placement> placements, PlayerGameState state);

}
