package qgame.rule.scoring;

import java.util.ArrayList;
import java.util.List;

import qgame.state.map.QGameMap;
import qgame.state.Placement;

public class MultiScoringRule implements ScoringRule {

  private final List<ScoringRule> rules;

  public MultiScoringRule(ScoringRule... rules) {
    this.rules = new ArrayList<>(List.of(rules));
  }
  public MultiScoringRule(List<ScoringRule> rules) {
    this.rules = new ArrayList<>(rules);
  }

  @Override
  public int pointsFor(List<Placement> placements, QGameMap map) {
    return rules.stream().mapToInt(rule -> rule.pointsFor(placements, map)).sum();
  }
}
