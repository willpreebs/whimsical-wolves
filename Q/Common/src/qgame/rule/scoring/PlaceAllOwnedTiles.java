package qgame.rule.scoring;

import java.util.List;

import qgame.state.map.IMap;
import qgame.state.Placement;

/**
 * Represents a scoring rule that awards bonus points if a player places all tiles that they own in a
 * game of QGame.
 */
public class PlaceAllOwnedTiles implements ScoringRule {

  private final int ALL_TILES_BONUS;
  private final int tileCount;

  public PlaceAllOwnedTiles(int tileCount, int bonus) {
    this.ALL_TILES_BONUS = bonus;
    this.tileCount = tileCount;
  }
  
  @Override
  public int pointsFor(List<Placement> placements, IMap map) {
    return placements.size() == tileCount ? ALL_TILES_BONUS : 0;
  }
}
