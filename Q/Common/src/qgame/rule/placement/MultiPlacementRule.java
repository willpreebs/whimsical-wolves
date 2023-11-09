package qgame.rule.placement;

import java.util.ArrayList;
import java.util.List;

import qgame.state.Placement;
import qgame.state.IPlayerGameState;

/**
 * An and predicate of ARule, is satisfied if all sub-rules
 * are also satisfied.
 */
public class MultiPlacementRule extends ARule {

  private final List<PlacementRule> rules;

  public MultiPlacementRule(PlacementRule... rules) {
    this.rules = new ArrayList<>(List.of(rules));
  }
  public MultiPlacementRule(List<PlacementRule> rules) {
    this.rules = new ArrayList<>(rules);
  }

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState gameState) {
    return this.rules.stream().allMatch(rule -> rule.isPlacementListLegal(placements, gameState));
  }
}
