package qgame.rule.placement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qgame.state.Placement;
import qgame.state.IPlayerGameState;
import qgame.state.map.Tile;
import qgame.util.TileUtil;

/**
 * Given a list of placements and a map, checks
 * whether the player's hand contains all the tiles made in the
 * placement.
 */
public class CorrectPlayerTilesRule extends ARule {

  /**
   * Generates a Hashmap of Tile type to number of each type of tile
   * in a collection of tiles
   * @param list collection of tiles to sort.
   * @return
   */
  private Map<Tile, Integer> generateMapFromTiles(Collection<Tile> list) {
    Map<Tile, Integer> tileCount = new HashMap<>();
    list.forEach(tile -> tileCount.put(tile, 1 + tileCount.getOrDefault(tile, 0)));
    return tileCount;
  }

  /**
   * Checks whether a given tile type from placements is contained in the player's
   * hand and that the amount of that tile type used in placements is less than
   * how many the player has of that tile type.
   * @param tile tile type checked for
   * @param placements map of tile type to number of that tile used in placements.
   * @param playerTiles map of tile type to number of that tile in player hand.
   * @return
   */
  private boolean validTileCount(Tile tile, Map<Tile, Integer> placements,
                                 Map<Tile, Integer> playerTiles) {
    return playerTiles.containsKey(tile) && placements.get(tile) <= playerTiles.get(tile);
  }

  private boolean validTileCounts(Map<Tile, Integer> placementCounts,
                                  Map<Tile, Integer> playerCounts) {
    return placementCounts
      .keySet()
      .stream()
      .allMatch(tile -> validTileCount(tile, placementCounts, playerCounts));
  }

  @Override
  public boolean isPlacementListLegal(List<Placement> placements, IPlayerGameState state) {

    Map<Tile, Integer> placementsMap = generateMapFromTiles(TileUtil.placementsToTiles(placements));
    Map<Tile, Integer> playerTileMap = generateMapFromTiles(state.getCurrentPlayerTiles().getItems());
    return validTileCounts(placementsMap, playerTileMap);
  }
}
