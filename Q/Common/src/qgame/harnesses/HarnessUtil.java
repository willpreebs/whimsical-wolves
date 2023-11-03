package qgame.harnesses;

import java.util.List;

import qgame.rule.placement.CorrectPlayerTilesRule;
import qgame.rule.placement.ExtendSameLineRule;
import qgame.rule.placement.ExtendsBoardRule;
import qgame.rule.placement.MatchTraitRule;
import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.scoring.MultiScoringRule;
import qgame.rule.scoring.PlaceAllOwnedTiles;
import qgame.rule.scoring.PointPerContiguousSequenceRule;
import qgame.rule.scoring.PointPerTileRule;
import qgame.rule.scoring.QRule;
import qgame.rule.scoring.ScoringRule;

class HarnessUtil {

  public static PlacementRule createPlaceRules() {
    List<PlacementRule> rules = List.of( new MultiPlacementRule(new CorrectPlayerTilesRule(),
      new ExtendsBoardRule(),
      new ExtendSameLineRule(), new MatchTraitRule()));
    return new MultiPlacementRule(rules);
  }

  public static ScoringRule createScoreRules() {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(),
      new QRule(6),
      new PointPerContiguousSequenceRule());
    return new MultiScoringRule(rules);
  }
}
