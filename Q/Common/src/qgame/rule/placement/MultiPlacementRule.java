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

  public MultiPlacementRule getBoardRules() {
    List<PlacementRule> boardRules = new ArrayList<>();

    for (PlacementRule r : rules) {
      if (r instanceof BoardRule) {
        boardRules.add(r);
      }
    }
    return new MultiPlacementRule(boardRules);
  }

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState gameState) {
    //return this.rules.stream().allMatch(rule -> rule.isPlacementListLegal(placements, gameState));
    //System.out.println(gameState.getPlayerName());

    for (PlacementRule r : this.rules) {
      if (!r.isPlacementListLegal(placements, gameState)) {
        // System.out.println("Rule broken: " + r.getClass());
        return false;
      }
    }
    return true;
  }
}
