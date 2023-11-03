package qgame;

import java.util.HashMap;
import java.util.Map;

import qgame.state.map.Tile;
import qgame.state.map.TileImpl;

public class TestUtil {

  public static Map<Tile.Color, Map<Tile.Shape, Tile>> allTiles() {
    Map<Tile.Color, Map<Tile.Shape, Tile>> allTiles = new HashMap<>();
    for (Tile.Color color : Tile.Color.values()) {
      allTiles.put(color, new HashMap<>());
      for (Tile.Shape shape : Tile.Shape.values()) {
        allTiles.get(color).put(shape, new TileImpl(color, shape));
      }
    }
    return allTiles;
  }
}
