package qgame.rule.placement;

import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;

/**
 * Abstract class for Placement Rule that implements validPositionsForTile.
 */
public abstract class ARule implements IPlacementRule {

  /**
   * Returns a list of all the valid positions a tile can be placed on a given game state.
   * @param t the tile to be placed
   * @param gameState the current game state where the tile will be placed
   * @return a list of posns where a tile can be placed on at a given game state
   */
  @Override
  public List<Posn> validPositionsForTile(Tile t, IPlayerGameState gameState) {
    List<Posn> validPosns = gameState.getBoard().validPositions();
    List<Placement> potentialValidPlacements = validPosns.stream().map(posn -> new Placement(posn, t)).toList();

    List<Posn> result = new ArrayList<>();
    for (Placement p : potentialValidPlacements) {
      List<Placement> s = List.of(p);
      if (this.isPlacementListLegal(s, gameState)) {
        result.add(p.posn());
      }
    }

    return result;
  }
}
