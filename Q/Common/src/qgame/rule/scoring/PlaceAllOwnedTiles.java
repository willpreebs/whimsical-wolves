package qgame.rule.scoring;

import java.util.List;

import qgame.state.map.QGameMap;
import qgame.state.Placement;

/**
 * Represents a scoring rule that awards 6 points if a player places all tiles that they own in a
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
  public int pointsFor(List<Placement> placements, QGameMap map) {
    return placements.size() == tileCount ? BONUS : 0;
  }
}
