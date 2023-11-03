package qgame.rule.placement;

import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.state.Placement;
import qgame.state.PlayerGameState;

/**
 * Represents a rule about placements on a QGameBoard.
 */
public interface PlacementRule {

  /**
   * Tests if a series of placements satisfies the rule this class represenets on the given
   * QGameMap.
   * @param placements The series of placements. The placements are in order with the first
   *                   element being the first placement the second element being the second
   *                   placement, etc.
   * @param gameState The game state to test the rules on.
   * @return True if the given placements satisify the rule.
   */
  boolean validPlacements(List<Placement> placements, PlayerGameState gameState);

  /**
   * Returns a list of all the valid positions a tile can be placed on a given game state.
   * @param t the tile to be placed
   * @param gameState the current game state where the tile will be placed
   * @return a list of posns where a tile can be placed on at a given game state
   */
  List<Posn> validPositionsForTile(Tile t, PlayerGameState gameState);
}
