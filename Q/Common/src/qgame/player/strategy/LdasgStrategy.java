package qgame.player.strategy;

import java.util.ArrayList;
import java.util.List;

import qgame.state.map.Posn;
import qgame.state.map.IMap;
import qgame.state.map.Tile;
import qgame.rule.placement.PlacementRule;
import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.util.PosnUtil;

/**
 * Represents a player strategy in which the smallest tile that can extend
 * the board is selected to be placed. If there are multiple placements for this
 * smallest tile, it selects the location with the most tile neighbors. If there
 * are further ties, then it selects the smallest per rowcolumn strategy.
 */
public class LdasgStrategy extends SmallestRowColumnTileStrategy {

  public LdasgStrategy(PlacementRule rules) {
    super(rules);
  }

  private List<Posn> neighborsWithTiles(Posn posn, IMap state) {
    return PosnUtil.neighbors(posn)
      .stream()
      .filter(state::posnHasTile)
      .toList();
  }

  private int neighborsWithTilesSize(Posn posn, IMap state) {
    return neighborsWithTiles(posn, state).size();
  }
  private int maxConstrained(List<Posn> posns, IMap board) {
    return posns
      .stream()
      .map(posn -> this.neighborsWithTiles(posn, board))
      .map(List::size)
      .reduce(0, Math::max);
  }
  private List<Posn> maxConstrainedNeighbors(List<Posn> posns, IMap board) {
    int maxConstrainSize = maxConstrained(posns, board);
    return new ArrayList<>(posns
        .stream()
        .filter(posn -> neighborsWithTilesSize(posn, board) == maxConstrainSize)
        .toList());
  }

  private Posn bestPosition(List<Posn> posns, IMap board) {
    List<Posn> maxConstrainedPositions = maxConstrainedNeighbors(posns, board);
    maxConstrainedPositions.sort(PosnUtil::rowColumnCompare);
    return maxConstrainedPositions.get(0);
  }

  protected Placement getBestPlacement(IPlayerGameState state, List<Posn> legalPlaces, Tile bestTile) {
    Posn bestPosn = bestPosition(legalPlaces, state.getBoard());
    return new Placement(bestPosn, bestTile);
  }
}