package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.Tile;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.util.PosnUtil;

/**
 * Represents a strategy where the player looks for the smallest tile
 * that can extend the current board. If there are multiple tiles of
 * equally small value (as determined in the Tile Util comparator), it
 * picks the one with the smallest row-column order.
 */
public class DagStrategy extends SmallestRowColumnTileStrategy {
  
  public DagStrategy(PlacementRule ruleBook) {
    super(ruleBook);
  }

  /**
   * Returns the best Placement determined by a given list of Posns assumed to be legal.
   * The best Placement is determined by the smallest row column order of all the Posns
   */
  protected Placement getBestPlacement(IPlayerGameState currentState, List<Posn> legalPosns, Tile bestTile) {
    // Tile bestTile = this.bestTile(currentState);
    ArrayList<Posn> legal = new ArrayList<>(legalPosns);
    legal.sort(PosnUtil::rowColumnCompare);
    Posn posn = legal.get(0);
    return new Placement(posn, bestTile);
  }
}