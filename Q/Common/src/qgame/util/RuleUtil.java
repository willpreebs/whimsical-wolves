package qgame.util;

import java.util.List;

import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.PlacementRule;
import qgame.rule.placement.board.ExtendsBoardRule;
import qgame.rule.placement.board.MatchTraitRule;
import qgame.rule.placement.move.ExtendSameLineRule;
import qgame.rule.placement.state.CorrectPlayerTilesRule;
import qgame.rule.scoring.MultiScoringRule;
import qgame.rule.scoring.PlaceAllOwnedTiles;
import qgame.rule.scoring.PointPerContiguousSequenceRule;
import qgame.rule.scoring.PointPerTileRule;
import qgame.rule.scoring.QRule;
import qgame.rule.scoring.ScoringRule;

public class RuleUtil {

  private static final int ALL_TILE_BONUS = 4;
  private static final int Q_BONUS = 8;
  private static final int POINTS_PER_TILE = 1;
  private static final int POINTS_PER_CONTIGUOUS_TILE = 1;

  public static PlacementRule createPlaceRules() {
    List<PlacementRule> rules = List.of(new CorrectPlayerTilesRule(),
      new ExtendsBoardRule(),
      new ExtendSameLineRule(), new MatchTraitRule());
    return new MultiPlacementRule(rules);
  }

  public static ScoringRule createScoreRules(int numberPlayerTiles) {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(POINTS_PER_TILE),
      new QRule(Q_BONUS),
      new PointPerContiguousSequenceRule(POINTS_PER_CONTIGUOUS_TILE),
      new PlaceAllOwnedTiles(ALL_TILE_BONUS, numberPlayerTiles));
    return new MultiScoringRule(rules);
  }

  /**
   * For reverse compatibility
   * Q bonus is always assumed to be 6
   * Place all owned tiles rule is omitted
   * @return
   */
  public static ScoringRule createOldScoreRules() {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(POINTS_PER_TILE),
      new QRule(6),
      new PointPerContiguousSequenceRule(POINTS_PER_CONTIGUOUS_TILE));
    return new MultiScoringRule(rules);
  }
}
