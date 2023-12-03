package qgame.rule.scoring;

import java.util.List;

import qgame.state.IGameState;
import qgame.state.Placement;

/**
 * Represents a rule that awards 1 point for each tile placed in a given list of placements.
 */
public class PointPerTileRule implements ScoringRule {

  private final int POINTS_PER_TILE;

  // public PointPerTileRule() {
  //   POINTS_PER_TILE = DEFAULT;
  // }

  public PointPerTileRule(int pointsPerTile) {
    POINTS_PER_TILE = pointsPerTile;
  }

  @Override
  public int pointsFor(List<Placement> placements, IGameState state) {
    return placements.size() * POINTS_PER_TILE;
  }
}
