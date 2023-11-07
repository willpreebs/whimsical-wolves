package qgame.rule.scoring;

import java.util.List;

import qgame.state.map.IMap;
import qgame.state.Placement;

/**
 * Represents a rule that awards 1 point for each tile placed in a given list of placements.
 */
public class PointPerTileRule implements ScoringRule{
  @Override
  public int pointsFor(List<Placement> placements, IMap map) {
    return placements.size();
  }
}
