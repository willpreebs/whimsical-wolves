package qgame.rule.placement;

import java.util.ArrayList;
import java.util.List;

import qgame.state.Placement;
import qgame.state.PlayerGameState;

public class MultiPlacementRule extends ARule {

  private final List<PlacementRule> rules;

  public MultiPlacementRule(PlacementRule... rules) {
    this.rules = new ArrayList<>(List.of(rules));
  }
  public MultiPlacementRule(List<PlacementRule> rules) {
    this.rules = new ArrayList<>(rules);
  }

  @Override
  public boolean validPlacements(List<Placement> placements, PlayerGameState gameState) {
    return this.rules.stream().allMatch(rule -> rule.validPlacements(placements, gameState));
  }
}
