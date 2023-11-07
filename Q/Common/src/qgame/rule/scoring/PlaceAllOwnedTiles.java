package qgame.rule.scoring;

import java.util.List;

import qgame.state.map.IMap;
import qgame.state.Placement;

/**
 * Represents a scoring rule that awards bonus points if a player places all tiles that they own in a
 * game of QGame.
 */
public class PlaceAllOwnedTiles implements ScoringRule {

  private final int BONUS;
  private final int tileCount;

  public PlaceAllOwnedTiles(int tileCount, int bonus) {
    this.BONUS = bonus;
    this.tileCount = tileCount;
  }
  @Override
  public int pointsFor(List<Placement> placements, IMap map) {
    return placements.size() == tileCount ? BONUS : 0;
  }
}
