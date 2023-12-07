package qgame.rule.scoring;

import java.util.List;

import qgame.state.IGameState;
import qgame.state.Placement;

/**
 * Represents a scoring rule that awards bonus points if a player places all tiles that they own in a
 * game of QGame.
 */
public class PlaceAllOwnedTiles implements IScoringRule {

  private final int ALL_TILES_BONUS;

  public PlaceAllOwnedTiles(int bonus) {
    this.ALL_TILES_BONUS = bonus;
  }
  
  @Override
  public int pointsFor(List<Placement> placements, IGameState state) {
    int numPlayerTiles = state.getCurrentPlayerInfo().getTiles().size();
    return placements.size() == numPlayerTiles ? ALL_TILES_BONUS : 0;
  }
}
