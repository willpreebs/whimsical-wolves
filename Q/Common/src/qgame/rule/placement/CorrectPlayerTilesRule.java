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
 * Checks given a list of placements and a map,
 * whether all the tiles in the placements exists in the player's hand
 */
public class CorrectPlayerTilesRule extends ARule {

  private Map<Tile, Integer> generateMapFromTiles(Collection<Tile> list) {
    Map<Tile, Integer> tileCount = new HashMap<>();
    list.forEach(tile -> tileCount.put(tile, 1 + tileCount.getOrDefault(tile, 0)));
    return tileCount;
  }

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
