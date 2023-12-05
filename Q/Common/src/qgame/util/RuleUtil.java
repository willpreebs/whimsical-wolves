package qgame.util;

import java.util.List;

import qgame.rule.placement.MultiPlacementRule;
import qgame.rule.placement.IPlacementRule;
import qgame.rule.placement.board.BoardRule;
import qgame.rule.placement.board.ExtendsBoardRule;
import qgame.rule.placement.board.MatchTraitRule;
import qgame.rule.placement.board.MultiBoardRule;
import qgame.rule.placement.move.ExtendSameLineRule;
import qgame.rule.placement.move.MoveRule;
import qgame.rule.placement.state.CorrectPlayerTilesRule;
import qgame.rule.placement.state.StateRule;
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

  public static IPlacementRule createPlaceRules() {
    List<IPlacementRule> rules = List.of(new CorrectPlayerTilesRule(),
      new ExtendsBoardRule(),
      new ExtendSameLineRule(), new MatchTraitRule());
    return new MultiPlacementRule(rules);
  }

  public static BoardRule createBoardRules() {
    List<BoardRule> rules = List.of(new ExtendsBoardRule(), new MatchTraitRule());
    return new MultiBoardRule(rules);
  }

  public static MoveRule createMoveRules() {
    return new ExtendSameLineRule();
  }

  public static StateRule createStateRules() {
    return new CorrectPlayerTilesRule();
  }

  /**
   * Creates a ScoringRule containing all of the scoring rules with default point
   * values
   */
  public static ScoringRule createScoreRules() {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(POINTS_PER_TILE),
      new QRule(Q_BONUS),
      new PointPerContiguousSequenceRule(POINTS_PER_CONTIGUOUS_TILE),
      new PlaceAllOwnedTiles(ALL_TILE_BONUS));
    return new MultiScoringRule(rules);
  }

  public static ScoringRule createScoreRules(int qBonus, int fBonus) {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(POINTS_PER_TILE),
      new QRule(qBonus),
      new PointPerContiguousSequenceRule(POINTS_PER_CONTIGUOUS_TILE),
      new PlaceAllOwnedTiles(fBonus));
    return new MultiScoringRule(rules);
  }
 
  //TODO: take in config file
  public static ScoringRule createScoreRules(int pointsPerTile, int qBonus, int pointsPerContiguousTile, int allTilesBonus) {
    List<ScoringRule> rules = List.of(
      new PointPerTileRule(pointsPerTile),
      new QRule(qBonus),
      new PointPerContiguousSequenceRule(pointsPerContiguousTile),
      new PlaceAllOwnedTiles(allTilesBonus));
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
